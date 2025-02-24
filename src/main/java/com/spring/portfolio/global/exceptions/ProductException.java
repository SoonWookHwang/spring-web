package com.spring.portfolio.global.exceptions;


import org.springframework.http.HttpStatus;

public class ProductException extends CustomException {
    public ProductException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
