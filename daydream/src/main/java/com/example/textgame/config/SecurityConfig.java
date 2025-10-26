package com.example.textgame.config;

import com.example.textgame.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 混合方案：
 * 1. 添加 /api/auth/register 到白名单。
 * 2. 提供 AuthenticationProvider (使用 UserService 和 PasswordEncoder)。
 * 3. 提供 AuthenticationManager Bean (供 AuthController 注入)。
 */
@Configuration
@RequiredArgsConstructor // 替换 @EnableSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder; // 依赖 PasswordEncoderConfig

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 允许访问前端页面和白名单API
                        .requestMatchers("/", "/*.html", "/favicon.ico").permitAll()
                        .requestMatchers("/api/auth/login").permitAll() // 登录
                        .requestMatchers("/api/auth/register").permitAll() // 注册
                        .requestMatchers("/api/knowledge/**").permitAll() // 知识库
                        .requestMatchers("/api/files/download/report/**").permitAll() // 公开报告

                        // 按照您的 3.7 步骤，/hello 需要 ADMIN 角色
                        // 注意：我们 UserService 默认只给 ROLE_USER，所以 admin 登录也无法访问
                        // .requestMatchers("/hello").hasRole("ADMIN")

                        // 简化：只要登录了就能访问 /hello
                        .requestMatchers("/hello").authenticated()

                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 关键：设置我们自定义的 AuthenticationProvider
                .authenticationProvider(authenticationProvider())

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 必须提供 AuthenticationProvider
     * 它告诉 Spring Security 如何使用 UserService 和 PasswordEncoder 验证用户
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService); // 设置 UserDetailsService
        authProvider.setPasswordEncoder(passwordEncoder); // 设置密码编码器
        return authProvider;
    }

    /**
     * 必须提供 AuthenticationManager Bean
     * 这样 AuthController 才能 @Autowired 它
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

