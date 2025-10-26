package com.example.textgame.controller;

import com.example.textgame.config.JwtUtil;
import com.example.textgame.dto.AuthRequest;
import com.example.textgame.dto.AuthResponse;
import com.example.textgame.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 混合方案：
 * 1. 注入 AuthenticationManager 来验证用户。
 * 2. 注入 UserService 来注册新用户。
 * 3. 注入 JwtUtil 来生成令牌。
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 登录 API
     * (不再硬编码 "admin"，而是使用 Spring Security 验证)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            // 1. 使用 AuthenticationManager 验证用户名和密码
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            // 2. 验证失败
            return ResponseEntity.status(401).body("用户名或密码错误");
        }

        // 3. 验证成功，从 UserService 加载用户详情（确保一致性）
        final UserDetails userDetails = userService.loadUserByUsername(request.getUsername());

        // 4. 生成 JWT
        final String token = jwtUtil.generateToken(userDetails.getUsername());

        // 5. 返回 Token
        return ResponseEntity.ok(new AuthResponse(token));
    }

    /**
     * 注册 API
     * (使用 UserService 和 PasswordEncoder)
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest request) {
        try {
            userService.registerUser(request.getUsername(), request.getPassword());
            return ResponseEntity.ok("注册成功！");
        } catch (IllegalArgumentException e) {
            // 捕获 "用户名已存在" 异常
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("注册时发生内部错误");
        }
    }
}

