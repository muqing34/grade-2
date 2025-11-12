package com.example.textgame.repository;

import com.example.textgame.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * (新) Spring Data JPA 仓库，用于替代 InMemoryUserRepository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA 会自动实现这个方法
    // 等同于: SELECT * FROM app_user WHERE username = ?
    Optional<User> findByUsername(String username);

    // Spring Data JPA 也会自动实现这个方法
    // 等同于: SELECT COUNT(*) > 0 FROM app_user WHERE username = ?
    boolean existsByUsername(String username);
}