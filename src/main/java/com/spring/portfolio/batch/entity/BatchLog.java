package com.spring.portfolio.batch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BatchLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long batchLogId;

  private String jobName;

  private String stepName;

  private String status;

  private LocalDateTime completeDate;

}
