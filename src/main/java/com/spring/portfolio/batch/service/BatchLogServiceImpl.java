package com.spring.portfolio.batch.service;

import com.spring.portfolio.batch.entity.BatchLog;
import com.spring.portfolio.batch.repository.BatchLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BatchLogServiceImpl implements BatchLogService{

  private final BatchLogRepository batchLogRepository;


  @Override
  public void insertBatchLog(BatchLog log) {
    batchLogRepository.save(log);
  }

  @Override
  public List<BatchLog> getBatchLog() {
    return batchLogRepository.findAll();
  }
}
