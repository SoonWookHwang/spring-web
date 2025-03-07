package com.spring.portfolio.batch.config;

import com.spring.portfolio.batch.component.EmailBatchTasklet;
import java.util.List;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
//@EnableBatchProcessing  //-- 스프링 배치 5.xx 버전 이상에서는 DefaultBatchConfiguration 클래스를 상속받는것으로 변경됌
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class BatchConfig extends DefaultBatchConfiguration {

  private final DataSource dataSource;
  private final EmailBatchTasklet emailSendingTasklet;

  /*
  스프링 배치 5버전 이상에서는 batch관련 설정들이 인메모리 db의 자동생성방식에서 개발자가 데이터소스, 레포지토리, 트랜잭션 매니저를 직접 설정해주는 것으로 변경됌
  * 데이터베이스 테이블 자동 생성 설정하기
    스프링 부트 3에서 테이블을 자동으로 생성하는 또 다른 방법은 데이터베이스 테이블 자동 생성 설정을 활용하는 것이다.
    스프링 부트는 애플리케이션 시작 시 schema-@@platform@@.sql 스크립트를 자동으로 실행한다.
    예를 들어 PostgreSQL을 사용한다면, schema-postgres.sql 파일에 스프링 배치 테이블 생성에 필요한 SQL 명령어를 넣고,
    이 파일을 src/main/resources에 위치시키면 된다. (spring batch core 패키지 내부의 각 db에 맞는 sql 파일을 resources 바로 하위에 위치시키면 된다.)
  */
//  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;

  // Job 정의
  @Bean
  public Job sampleJob(JobRepository jobRepository, Step sampleStep) {
    return new JobBuilder("sampleJob", jobRepository)
        .start(sampleStep)
        .build();
  }
  @Bean
  public Step sampleStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("sampleStep", jobRepository)
        .<String, String>chunk(10, transactionManager) // 청크 크기 및 트랜잭션 매니저 지정
        .reader(itemReader())		 // ItemReader 구현체
        .processor(itemProcessor())	// ItemProcessor 구현체
        .writer(itemWriter()) 		// ItemWriter 구현체
        .build();
  }

  // ItemReader 구현체
  private ItemReader<String> itemReader() {
    log.info("itemReader call");
    return new ListItemReader<>(List.of("item1", "item2", "item3"));
  }

  // ItemProcessor 구현체
  private ItemProcessor<String, String> itemProcessor() {
    log.info("itemProcessor call");
    return item -> "Processed " + item;
  }

  // ItemWriter 구현체
  private ItemWriter<Object> itemWriter() {
    return items -> items.forEach(item -> log.info(item.toString()));
  }

  @Bean
  public Job emailJob(JobRepository jobRepository) {
    return new JobBuilder("emailJob",jobRepository)
        .start(emailStep(jobRepository))
        .build();
  }
  @Bean
  public Step emailStep(JobRepository jobRepository) {
    return new StepBuilder("emailStep", jobRepository)
        .tasklet(emailSendingTasklet, transactionManager)
        .build();
  }

//  @Bean
//  public PlatformTransactionManager transactionManager() {
//    return new DataSourceTransactionManager(dataSource);
//  }
//
//  @Bean
//  public JobRepository jobRepository() {
//    JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
//    factory.setDataSource(dataSource);
//    factory.setTransactionManager(transactionManager); // 여기서 직접 호출하지 않음
//    factory.setDatabaseType("MYSQL"); // 사용 중인 DB 종류 (MySQL, H2 등)
//    try {
//      return factory.getObject();
//    } catch (Exception e) {
//      throw new RuntimeException(e);
//    }
//  }


}
