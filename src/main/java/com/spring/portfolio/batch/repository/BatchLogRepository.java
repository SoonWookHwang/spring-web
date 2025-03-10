package com.spring.portfolio.batch.repository;

import com.spring.portfolio.batch.entity.BatchLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchLogRepository extends JpaRepository<BatchLog,Long> {

}
