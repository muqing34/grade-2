package com.example.textgame.repository;

import com.example.textgame.model.GameState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameStateRepository extends JpaRepository<GameState, Long> {

    // 我们将不再按 userId (String) 查找，而是按 User ID (Long) 查找
    // JpaRepository 已经提供了 findById(Long id)

    // 我们需要一个方法来通过 User 对象查找
    Optional<GameState> findByUser(com.example.textgame.model.User user);

    // 或者通过用户 ID (它也是 GameState 的 ID)
    // findById(Long userId) 已经由 JpaRepository 提供了
}