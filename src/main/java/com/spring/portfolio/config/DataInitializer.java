package com.spring.portfolio.config;

import com.spring.portfolio.jpa.entity.Customer;
import com.spring.portfolio.jpa.entity.Product;
import com.spring.portfolio.jpa.entity.ProductCategory;
import com.spring.portfolio.jpa.repository.CustomerRepository;
import com.spring.portfolio.jpa.repository.OrderRepository;
import com.spring.portfolio.jpa.repository.ProductCategoryRepository;
import com.spring.portfolio.jpa.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductCategoryRepository categoryRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> insertTestData();
    }

    @Transactional
    public void insertTestData() {
        // 1. Customer 데이터 삽입
        List<Customer> users = IntStream.range(0, 1000)
            .mapToObj(i -> new Customer(null, "User" + i, "user" + i + "@example.com", null))
            .collect(Collectors.toList());
        customerRepository.saveAll(users);

        // 2. ProductCategory 데이터 삽입 (상위-하위 카테고리 설정)
        ProductCategory electronics = new ProductCategory(null, "Electronics", null, new ArrayList<>());
        ProductCategory smartphones = new ProductCategory(null, "Smartphones", electronics, new ArrayList<>());
        ProductCategory televisions = new ProductCategory(null, "Television", electronics, new ArrayList<>());
        ProductCategory laptops = new ProductCategory(null, "Laptops", electronics, new ArrayList<>());
        ProductCategory android = new ProductCategory(null, "Android", smartphones, new ArrayList<>());
        ProductCategory iphone = new ProductCategory(null, "iPhone", smartphones, new ArrayList<>());

        // 상위 카테고리 저장
        categoryRepository.saveAll(Arrays.asList(electronics, smartphones, televisions, laptops));

        // 하위 카테고리 저장
        categoryRepository.saveAll(Arrays.asList(android, iphone));

        // 3. Product 데이터 삽입
        List<ProductCategory> categories = Arrays.asList(smartphones, televisions, laptops);
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            ProductCategory randomCategory = categories.get(ThreadLocalRandom.current().nextInt(categories.size()));
            products.add(new Product(null, "Product" + i, (int) (Math.random() * 1000) * 1000, (int) (Math.random() * 100), randomCategory));

            // 💡 매 100개씩 flush & clear 해서 메모리 절약
            if (i % 100 == 0) {
                productRepository.saveAll(products);
                productRepository.flush();  // 실제 DB에 반영
                products.clear(); // 영속성 컨텍스트 초기화
            }
        }
        // 남은 데이터 저장
        if (!products.isEmpty()) {
            productRepository.saveAll(products);
            productRepository.flush();
        }
    }
}