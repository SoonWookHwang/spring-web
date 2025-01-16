package com.spring.portfolio.jpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/jpa")
public class JpaRestController {

    @GetMapping(value = "/getProduct")
    public ResponseEntity<Map<String,Object>> getData(@RequestBody Map<String,Object> params){
        Map<String,Object> data = new HashMap<>();


    }
}
