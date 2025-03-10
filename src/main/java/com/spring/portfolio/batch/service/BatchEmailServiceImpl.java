package com.spring.portfolio.batch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchEmailServiceImpl implements BatchEmailService{

//  private final JavaMailSender mailSender;

  @Override
  public void sendEmail(String to, String subject, String text) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);
//    mailSender.send(message);
    log.info("메일 전송 테스트 : {}", message.toString());
  }
}
