package com.bohemio.miniblogapi.service;

import com.bohemio.miniblogapi.entity.Role;
import com.bohemio.miniblogapi.entity.User;
import com.bohemio.miniblogapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service("userDetailsService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername( username )
                .orElseThrow( () -> new UsernameNotFoundException( "해당 아이디를 찾을 수 없습니다: " + username ) );

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getEnabled(), // 계정 활성화 상태
                user.getAccountNonExpired(), // 계정 만료 여부
                user.getCredentialsNonExpired(), // 자격 증명 만료 여부
                user.getAccountNonLocked(), // 계정 잠김 여부
                mapRolesToAuthorities( user.getRoles() ) // 권한 정보
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect( Collectors.toList());
    }

    }
