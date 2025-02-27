package com.spring.portfolio.global.exceptions;

import org.springframework.http.HttpStatus;

public class CustomSecurityException extends CustomException{

  public CustomSecurityException(String message) {
    super(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
