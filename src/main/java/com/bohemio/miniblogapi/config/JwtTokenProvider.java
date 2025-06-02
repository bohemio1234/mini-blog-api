package com.bohemio.miniblogapi.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final SecretKey secretKey;
    private final long tokenValidityInSeconds;
    private final long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKeyString, @Value("${jwt.expiration-in-ms}") long tokenValidityInSeconds, @Value("${jwt.refresh-token-expiration-in-ms}") long refreshTokenValidityInMilliseconds) {
        this.secretKey = Keys.hmacShaKeyFor( secretKeyString.getBytes() ); //string을 바이트로. 키는 보통 바이트배열형태라서.
        this.tokenValidityInSeconds = tokenValidityInSeconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
        logger.info("비밀키 로딩 완료!");
    }

    public String createAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map( GrantedAuthority::getAuthority )
                .collect( Collectors.joining( "," ) ); // = "ROLE_USER,ROLE_ADMIN"
        //이걸 왜 이렇게 햐냐면, claim엔 보통 문자열이 들어가야하므로.
        //authentication.getAuthorities(): 사용자가 가진 모든 GrantedAuthority 객체들 꺼냄.
        //각 GrantedAuthority 객체에 대해 getAuthority() 메소드를 호출해서 실제 권한 이름(ex) "ROLE_USER", "ROLE_ADMIN") 뽑음.
        //.collect(Collectors.joining(",")): 이렇게 뽑아낸 권한 문자열들을 콤마(,)로 구분해서, "ROLE_USER,ROLE_ADMIN" 같은 문자열을 만들고 이 문자열을 나중에 JWT의 페이로드(내용물)에 담는데 사용함.

        Date now = new Date(); //현재 시간(발급시간)
        Date validity = new Date( now.getTime() + this.tokenValidityInSeconds ); //만료시간

        Claims claims = Jwts.claims()
                .subject(authentication.getName())
                .add("auth", authorities)
                .issuedAt( now )
                .expiration( validity )
                .build();

        return Jwts.builder()
                .claims(claims) //구성된 claims 주입
                .signWith( secretKey ) //secretkey만 전달해도 JJWT가 알아서 적절한 암호화 알고리즘 추론.
                .compact();
    } //그니까 이 함수는 로그인할떄 딱 한번 실행됨..
    //

    //밑부턴 위에 만들어진 JWT가 진짜인지, 유효한지 검증하고, 그 안에 숨겨진 정보들을 뽑아내는 메소드
    //즉, 프론트에서 이제 localStorage든 어디든 저장해서 String token으로 쏴줌. 이것들을 받고 행동하는 친구들.

// === 새로운 메소드: 리프레시 토큰 생성 ===
    // 리프레시 토큰은 특별한 클레임 없이, 식별자와 만료 시간만 가져도 충분한 경우가 많다.
    // 여기서는 간단하게 UUID를 토큰 값으로 사용하고, JWT 형식으로 감싸서 만료 시간을 포함시킨다.
    // 또는, 그냥 UUID 문자열 자체를 DB에 저장하고, JWT 형식으로 만들지 않아도 된다.
    // 여기서는 JWT 형식으로 만들어 만료 시간 관리를 JWT에 맡기는 방식을 보여주겠다.

    public String createRefreshToken(Authentication authentication) { // 또는 String username을 파라미터로 받을 수도 있다.
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.refreshTokenValidityInMilliseconds);

        // 리프레시 토큰에는 사용자 이름 정도만 넣거나, 아예 아무 클레임도 안 넣을 수도 있다.
        // 여기서는 액세스 토큰과 구분하기 위해 다른 클레임 구조를 가질 수 있음을 보여준다.
        // 혹은 정말 간단하게는 jti (JWT ID)만으로도 구성 가능.
        Claims claims = Jwts.claims()
                .subject(authentication.getName()) // 누구의 리프레시 토큰인지 식별
                .issuedAt(now)
                .expiration(validity)
                .id( UUID.randomUUID().toString()) // 리프레시 토큰 자체의 고유 ID (선택적)
                .build();

        // 리프레시 토큰은 내용이 중요하기보다는 그 자체의 존재와 유효성이 중요.
        return Jwts.builder()
                .claims(claims)
                .signWith(secretKey) // 동일한 키로 서명 (또는 다른 키 사용도 가능)
                .compact();
    }


    // 토큰에서 Claims 추출 (내부적으로 서명 검증 포함)
    private Claims getClaimsFromToken(String token) throws JwtException {
        return Jwts.parser() // 토큰을 해석하고 검증할 준비시작
                .verifyWith(secretKey) //토큰 서이 비밀키랑 맞는지
                .build()
                .parseSignedClaims(token) //실질적인검증과정.
                .getPayload();            // getBody() 대신 getPayload()
    }

    public String getUsername(String token) {
        try {
            return getClaimsFromToken(token).getSubject();
        } catch (ExpiredJwtException e) {
            logger.warn("만료된 JWT 토큰입니다: {}", e.getMessage());
            return e.getClaims().getSubject();
        } catch (JwtException e) {
            logger.warn("유효하지 않은 JWT 토큰입니다 (getUsername): {}", e.getMessage());
            return null;
        }
    }

    // 토큰에서 인증 정보(Authentication 객체) 조회
    public Authentication getAuthentication(String token) {

        Claims claims = getClaimsFromToken(token);
        String username = claims.getSubject();

        // 우리가 "auth"라는 이름으로 저장했던 권한 정보 문자열을 꺼내서,
        //  실제 GrantedAuthority 객체들의 컬렉션으로 변환(springsecurity가 이해할수있게)
        //split(",")하면 문자열에잇는내용을 ,기준으로 배열로 나눠주는것.
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get( "auth", String.class ).split( "," ) ) // "auth" 클레임을 문자열로 가져와 쉼표로 쪼갬
                        .filter( auth -> !auth.trim().isEmpty() ) // 혹시 모를 빈 문자열 제거
                        .map( SimpleGrantedAuthority::new ) // 각 권한 문자열을 SimpleGrantedAuthority 객체로 변환 (이건 GrantedAuthority구현체)
                        .collect( Collectors.toList() );    // 리스트로 모음

        UserDetails userDetails = new User( username, "", authorities );

        // 최종적으로 Authentication 객체를 만들어서 반환!
        return new UsernamePasswordAuthenticationToken( userDetails, "", authorities );

    }

    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("유효하지 않은 JWT 토큰입니다.", e); // logger는 SLF4J 같은 로깅 프레임워크 사용
        }
        return false;
    }

    // 리프레시 토큰의 만료 시간을 가져오는 메소드 (DB에 저장된 토큰을 검증할 때 사용 가능)
    public Date getExpiryDateFromToken(String token) {
        try {
            return getClaimsFromToken(token).getExpiration();
        } catch (JwtException e) {
            logger.warn("유효하지 않은 JWT 토큰입니다 (getExpiryDateFromToken): {}", e.getMessage());
            return null;
        }
    }

}
