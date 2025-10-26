package com.example.textgame.repository;
 
 import com.example.textgame.model.User;
 import org.springframework.stereotype.Repository;
 
 import java.util.Map;
 import java.util.Optional;
 import java.util.concurrent.ConcurrentHashMap;
 
 @Repository
 public class InMemoryUserRepository {
     // 使用 ConcurrentHashMap 保证线程安全
     private final Map<String, User> userStore = new ConcurrentHashMap<>();
 
     public Optional<User> findByUsername(String username) {
         return Optional.ofNullable(userStore.get(username));
     }
 
     public User save(User user) {
         userStore.put(user.getUsername(), user);
         return user;
     }
 
     public boolean existsByUsername(String username) {
         return userStore.containsKey(username);
     }
 }