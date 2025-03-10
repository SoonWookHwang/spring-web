package com.spring.portfolio.batch.component;

import com.spring.portfolio.batch.entity.BatchLog;
import com.spring.portfolio.batch.service.BatchEmailService;
import com.spring.portfolio.batch.service.BatchLogService;
import com.spring.portfolio.security.entity.PortfolioUser;
import com.spring.portfolio.security.service.PortfolioUserService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailBatchTasklet implements Tasklet {

  private final BatchEmailService emailService;
  private final PortfolioUserService userService; // 사용자 목록 조회 서비스

  private final BatchLogService batchLogService;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    List<PortfolioUser> users = userService.getUsersToEmail(); // 이메일을 보낼 사용자 목록 조회
    if (users.size() > 0) {
      for (PortfolioUser user : users) {
        try {
          String subject = "안녕하세요, " + user.getUsername() + "님!";
          String text = "배치 이메일 발송 테스트입니다.";
          emailService.sendEmail(user.getEmail(), subject, text);
          log.info("이메일 발송 성공: " + user.getEmail());
          BatchLog emailBatchLog = new BatchLog();
          emailBatchLog.setJobName("emailJob");
          emailBatchLog.setStatus("successes-finish");
          emailBatchLog.setCompleteDate(LocalDateTime.now());
          emailBatchLog.setStepName("emailStep");
          batchLogService.insertBatchLog(emailBatchLog);

        } catch (Exception e) {
          BatchLog emailBatchLog = new BatchLog();
          emailBatchLog.setJobName("emailJob");
          emailBatchLog.setStatus("error");
          emailBatchLog.setCompleteDate(LocalDateTime.now());
          emailBatchLog.setStepName("emailStep");
          batchLogService.insertBatchLog(emailBatchLog);
          log.warn("이메일 발송 실패: " + user.getEmail());
          // 실패한 이메일 재시도 로직 또는 관리자에게 알림 로직 추가 가능
        }
      }
      return RepeatStatus.FINISHED;
    }else{
      return RepeatStatus.FINISHED;
    }
  }
}