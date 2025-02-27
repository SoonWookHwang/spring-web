package com.spring.portfolio.global.exceptions;


import org.springframework.http.HttpStatus;

public class CustomProductException extends CustomException {
    public CustomProductException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
