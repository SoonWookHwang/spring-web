package com.spring.portfolio.batch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

@Slf4j
public class CustomChunkListener implements ChunkListener {

  @Override
  public void beforeChunk(ChunkContext context) {
    log.info(context.getStepContext().getJobName()+" : 새로운 청크 시작");
  }

  @Override
  public void afterChunk(ChunkContext context) {
    log.info(context.getStepContext().getJobName()+" :  청크 완료");

  }

  @Override
  public void afterChunkError(ChunkContext context) {
    log.error(context.getStepContext().getJobName()+" : 청크 처리 중 오류 발생");
  }
}