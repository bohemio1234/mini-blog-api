package com.bohemio.miniblogapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

}
