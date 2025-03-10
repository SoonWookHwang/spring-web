package com.spring.portfolio.batch.controller;

import com.spring.portfolio.batch.service.BatchLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/batch")
public class BatchRestController {

  private final BatchLogService batchLogService;

  @GetMapping("/")
  public ResponseEntity<?> getBatchLog(){
    return ResponseEntity.ok(batchLogService.getBatchLog());
  }

}
