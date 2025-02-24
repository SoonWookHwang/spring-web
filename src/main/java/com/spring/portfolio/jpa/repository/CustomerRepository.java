package com.spring.portfolio.jpa.repository;

import com.spring.portfolio.jpa.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
