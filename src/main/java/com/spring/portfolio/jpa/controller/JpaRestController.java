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

    /**
     검색 조건을 통한 데이터 조회
     페이징 처리 및 정렬 적용
     */
    @GetMapping(value = "/getProduct")
    public ResponseEntity<Map<String,Object>> getData(@RequestBody Map<String,Object> params){
        Map<String,Object> data = new HashMap<>();
        return ResponseEntity.ok(data);
    }
}
