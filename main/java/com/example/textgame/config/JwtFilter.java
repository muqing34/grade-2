package com.example.textgame.config;

import com.example.textgame.service.UserService; // 导入 UserService
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor; // 切换到构造函数注入
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // 导入 UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 混合方案：
 * 1. 过滤器不再需要白名单逻辑（由 SecurityConfig 处理）。
 * 2. 过滤器使用 UserService (UserDetailsService) 来加载真实的用户信息。
 */
@Component
@RequiredArgsConstructor // 使用构造函数注入
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService; // 注入 UserDetailsService

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // Token 无效或过期
                logger.warn("JWT Token validation error: " + e.getMessage());
            }
        }

        // 核心逻辑：使用 UserService 加载用户
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 从 UserService (实现了 UserDetailsService) 加载用户
            UserDetails userDetails = this.userService.loadUserByUsername(username);

            // 验证 Token (这里假设 JwtUtil.validateToken 只检查签名和过期，不比较用户名)
            if (jwtUtil.validateToken(jwt)) {
                // 创建 Spring Security 的认证 Token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()); // 使用真实权限

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 设置安全上下文
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 继续过滤链
        filterChain.doFilter(request, response);
    }
}

