package com.spring.portfolio.batch.component;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
//@RequiredArgsConstructor
public class BatchScheduler {

  private final JobLauncher jobLauncher;
  private final Job sampleJob;
  private final Job emailJob;

  public BatchScheduler(JobLauncher jobLauncher,
      @Qualifier("sampleJob") Job sampleJob,
      @Qualifier("emailJob") Job emailJob) {
    this.jobLauncher = jobLauncher;
    this.sampleJob = sampleJob;
    this.emailJob = emailJob;
  }

  @Scheduled(cron = "0 * * * * *")
  public void runBatchJob() throws Exception {
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();
    jobLauncher.run(sampleJob, jobParameters);
  }

  @Scheduled(cron = "0 * * * * *")
  public void runBatchEmailJob() throws Exception {
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();
    jobLauncher.run(emailJob, jobParameters);
  }
}