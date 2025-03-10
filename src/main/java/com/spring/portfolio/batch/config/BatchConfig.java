package com.spring.portfolio.batch.config;

import com.spring.portfolio.batch.component.EmailBatchTasklet;
import com.spring.portfolio.batch.entity.BatchLog;
import com.spring.portfolio.batch.service.BatchLogService;
import com.spring.portfolio.jpa.entity.Product;
import com.spring.portfolio.jpa.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.List;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.Transformation;
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

  private final EmailBatchTasklet emailSendingTasklet;

  private final ProductRepository productRepository;

  private final BatchLogService batchLogService;

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
        .listener(new CustomJobListener(batchLogService))
        .build();
  }

  // itemReader, itemProcessor, itemWriter 방식은 많은 양의 데이터와 복잡한 처리 로직에 사용됌
  @Bean
  public Step sampleStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("sampleStep", jobRepository)
        .<Product,Product>chunk(100, transactionManager) // 청크 크기 및 트랜잭션 매니저 지정 청크 크기단위로 트랜잭션이 처리됌
        .reader(itemReader())		 // ItemReader 구현체
        .processor(itemProcessor())	// ItemProcessor 구현체
        .writer(itemWriter()) 		// ItemWriter 구현체
        .listener(new CustomChunkListener()) // 청크단위로 실행되는 것 확인
        .build();
  }

  // ItemReader 구현체
  private ItemReader<Product> itemReader() {
    List<Product> products = productRepository.findAll();
    log.info("itemReader call");
//    return new ListItemReader<>(List.of("item1", "item2", "item3"));
    return new ListItemReader<>(products);
  }

/*   ItemProcessor 구현체
  데이터 변환 (Transformation)

  데이터 형식을 변경하거나, 특정 필드를 조작할 때 사용
  예: String 데이터를 Integer로 변환, 날짜 포맷 변경
  데이터 필터링 (Filtering)

  특정 조건을 만족하는 데이터만 Writer로 전달
  null을 반환하면 해당 데이터는 Writer로 넘어가지 않음
  예: 품절된 상품은 배치 처리에서 제외
  비즈니스 로직 적용

  데이터에 대한 가공 로직을 수행
  예: 재고(stock) 감소, 가격 조정, 특정 규칙 적용*/
  private ItemProcessor<Product, Product> itemProcessor() {
    log.info("itemProcessor call");
    return product -> {
      if (product.getStock() > 0) {
        product.decreaseStockToBatch();
        return product;
      }
      log.info("재고가 없는 제품 ID: {}", product.getProductId());
      return null; // ✅ 재고가 0이면 Writer로 전달되지 않음
    };
  }

  // ItemWriter 구현체
  private ItemWriter<Product> itemWriter() {
//    BatchLog batchLog = new BatchLog();
//    batchLog.setStatus("success");
//    batchLog.setCompleteDate(LocalDateTime.now());
//    batchLog.setJobName("sampleJob");
//    batchLog.setStepName("sampleStep");
//    batchLogService.insertBatchLog(batchLog);
    return items-> {
      log.info("처리된 완료 수 : "+items.size());
    };
  }

  // 단일 작업 용 tasklet 처리 tasklet 방식은 단순하고 적은 데이터 양의 처리에 사용됌
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
}
