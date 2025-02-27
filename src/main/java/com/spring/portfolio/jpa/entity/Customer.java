package com.spring.portfolio.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 고객(Customer) 엔티티는 시스템 내에서 사용자를 나타냅니다.
 * <p>
 * 이 엔티티는 데이터베이스의 "Customer" 테이블과 매핑되며, 고객의 고유 식별자,
 * 이름, 이메일, 생성일자 정보를 저장합니다. 또한 고객이 생성한 주문(OrderProduct)과
 * 일대다 관계로 연결됩니다.
 * </p>
 *
 * <ul>
 *   <li><b>userId</b>: 고객 고유 식별자(pk)</li>
 *   <li><b>userName</b>: 고객 아이디</li>
 *   <li><b>email</b>: 고객 이메일 주소</li>
 *   <li><b>createdAt</b>: 계정 생성일</li>
 * </ul>
 */
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends TimeStamps{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;
    private String userName;
    private String email;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Orders> orders = new ArrayList<>();
}
