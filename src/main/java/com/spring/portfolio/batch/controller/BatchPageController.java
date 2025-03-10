package com.spring.portfolio.batch.controller;

import com.spring.portfolio.batch.service.BatchLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/batch")
@RequiredArgsConstructor
public class BatchPageController {
  private final BatchLogService batchLogService;

  @GetMapping("/log")
  public String batchLogPage(Model model){
    model.addAttribute("batchLogs", batchLogService.getBatchLog());
    return "batch/batch-log";
  }
}
