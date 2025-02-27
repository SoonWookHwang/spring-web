package com.spring.portfolio.global.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus status; // HTTP 상태 코드

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
