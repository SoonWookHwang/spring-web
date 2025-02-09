package com.spring.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PortfolioApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortfolioApplication.class, args);
    }
//    @Bean
//    public CommandLineRunner initData(CustomerRepository customerRepository, ProductRepository productRepository, OrderRepository orderRepository, ProductCategoryRepository categoryRepository) {
//        return args -> {
//            // 유저 생성
//            List<Customer> users = IntStream.range(0, 1000)
//                    .mapToObj(i -> new Customer(null, "User" + i, "user" + i + "@example.com", null))
//                    .collect(Collectors.toList());
//            customerRepository.saveAll(users);
//
//            // 카테고리 및 제품 생성
//            ProductCategory electronics = new ProductCategory(null, "Electronics", null, null);
//            ProductCategory smartphones = new ProductCategory(null, "Smartphones", electronics, null);
//            ProductCategory television = new ProductCategory(null, "Television", electronics, null);
//
//            categoryRepository.saveAll(Arrays.asList(electronics, smartphones, television));
//
//            List<ProductCategory> categories = Arrays.asList(smartphones, television);
//
//            List<Product> products = IntStream.range(0, 1000)
//                    .mapToObj(i -> {
//                        ProductCategory randomCategory = categories.get(ThreadLocalRandom.current().nextInt(categories.size()));
//                        return new Product(null, "Product" + i, (int) (Math.random() * 1000)* 1000, (int) (Math.random() * 100), randomCategory);
//                    })
//                    .collect(Collectors.toList());
//            productRepository.saveAll(products);
//        };
//    }
}
