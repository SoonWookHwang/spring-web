package com.spring.portfolio.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/security")
public class SecurityPageController {


  @GetMapping("/login")
  public String loginPage(){
    return "/security/login";
  }
  @GetMapping("/auth")
  public String authTestPage(){
    return "/security/authentication";
  }

}
