package com.spring.portfolio.batch.config;

import com.spring.portfolio.batch.entity.BatchLog;
import com.spring.portfolio.batch.service.BatchLogService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomJobListener implements JobExecutionListener {

  private final BatchLogService batchLogService;

  @Override
  public void beforeJob(JobExecution jobExecution) {
    BatchLog startBatchLog = new BatchLog();
    startBatchLog.setJobName(jobExecution.getJobInstance().getJobName());
    startBatchLog.setStatus("start");
    startBatchLog.setCompleteDate(LocalDateTime.now());
    Optional<StepExecution> firstStepExecution = jobExecution.getStepExecutions().stream().findFirst();
    firstStepExecution.ifPresent(stepExecution -> startBatchLog.setStepName(stepExecution.getStepName()));
    batchLogService.insertBatchLog(startBatchLog);
    log.info("🔹 배치 작업 시작 - Job ID: {}", jobExecution.getJobId());
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    BatchLog endBatchLog = new BatchLog();
    endBatchLog.setJobName(jobExecution.getJobInstance().getJobName());
    log.info("after job status : {}", jobExecution.getStatus().name());
    if(jobExecution.getStatus().equals(BatchStatus.COMPLETED)){
      endBatchLog.setStatus("successes-finished");
    }else{
      endBatchLog.setStatus("something problem occurred");
    }
    endBatchLog.setCompleteDate(LocalDateTime.now());
    Optional<StepExecution> firstStepExecution = jobExecution.getStepExecutions().stream().findFirst();
    firstStepExecution.ifPresent(stepExecution -> endBatchLog.setStepName(stepExecution.getStepName()));
    batchLogService.insertBatchLog(endBatchLog);
    log.info("✅ 배치 작업 종료 - Job 상태: {}", jobExecution.getStatus());
  }
}
