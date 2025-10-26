package com.example.textgame.repository;
 
 import com.example.textgame.model.GameState;
 import org.springframework.stereotype.Repository;
 
 import java.util.Map;
 import java.util.Optional;
 import java.util.concurrent.ConcurrentHashMap;
 
 @Repository
 public class InMemoryGameStateRepository {
 
     private final Map<String, GameState> stateStore = new ConcurrentHashMap<>();
 
     public Optional<GameState> findByUserId(String userId) {
         return Optional.ofNullable(stateStore.get(userId));
     }
 
     public GameState save(GameState gameState) {
         stateStore.put(gameState.getUserId(), gameState);
         return gameState;
     }
     
     public void deleteByUserId(String userId) {
         stateStore.remove(userId);
     }
 }