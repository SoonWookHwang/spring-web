package com.spring.portfolio.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Payment extends TimeStamps{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Orders orders;

    private LocalDateTime paymentDate;
    private Double amount;
    private String paymentMethod; // CREDIT_CARD, PAYPAL, etc.
}