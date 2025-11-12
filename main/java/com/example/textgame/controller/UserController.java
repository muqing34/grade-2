package com.example.textgame.controller;

import com.example.textgame.model.User;
import com.example.textgame.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前登录用户的基本信息
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(404).build();
        }
        // 出于安全，我们不返回整个 User 对象（尤其是密码）
        // 我们只返回需要的信息，比如用户名
        return ResponseEntity.ok(Map.of("username", user.getUsername()));
    }
}
