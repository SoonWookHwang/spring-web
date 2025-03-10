package com.spring.portfolio.batch.service;

public interface BatchEmailService {

  void sendEmail(String to, String subject, String text);

}
