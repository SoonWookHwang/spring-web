package com.spring.portfolio.jpa.repository;

import com.spring.portfolio.jpa.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {
}
