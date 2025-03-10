package com.spring.portfolio.batch.service;

import com.spring.portfolio.batch.entity.BatchLog;
import java.util.List;

public interface BatchLogService {

  void insertBatchLog(BatchLog log);

  List<BatchLog> getBatchLog();

}
