package com.spring.portfolio.security.reopository;

import com.spring.portfolio.security.entity.PortfolioUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioUserRepository extends JpaRepository<PortfolioUser,Long> {

  Optional<PortfolioUser> findByEmail(String email);
}
