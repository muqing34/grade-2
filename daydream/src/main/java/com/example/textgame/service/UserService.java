package com.example.textgame.service;

import com.example.textgame.model.User;
// 导入新的 UserRepository
import com.example.textgame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 导入事务注解

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    // 依赖注入已更改为 JPA Repository
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true) // 读取操作设为只读事务
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("未找到用户: " + username));

        // 为用户添加一个默认权限
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities
        );
    }

    @Transactional // 开启事务
    public User registerUser(String username, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 创建新用户 (注意：我们没有设置 ID，JPA 会在保存时自动生成)
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword);

        return userRepository.save(newUser);
    }

    @Transactional // 开启事务
    public User updateUserAvatar(String username, String filePath) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("未找到用户"));
        user.setAvatarPath(filePath);

        // JPA 的 save 方法在这里会执行更新 (merge) 操作，因为它处理的是一个已有 ID 的实体
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}package com.example.textgame.service;

import com.example.textgame.model.User;
// 导入新的 UserRepository
import com.example.textgame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 导入事务注解

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    // 依赖注入已更改为 JPA Repository
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true) // 读取操作设为只读事务
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("未找到用户: " + username));

        // 为用户添加一个默认权限
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities
        );
    }

    @Transactional // 开启事务
    public User registerUser(String username, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 创建新用户 (注意：我们没有设置 ID，JPA 会在保存时自动生成)
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword);

        return userRepository.save(newUser);
    }

    @Transactional // 开启事务
    public User updateUserAvatar(String username, String filePath) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("未找到用户"));
        user.setAvatarPath(filePath);

        // JPA 的 save 方法在这里会执行更新 (merge) 操作，因为它处理的是一个已有 ID 的实体
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}package com.example.textgame.service;

import com.example.textgame.model.User;
// 导入新的 UserRepository
import com.example.textgame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 导入事务注解

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    // 依赖注入已更改为 JPA Repository
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true) // 读取操作设为只读事务
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("未找到用户: " + username));

        // 为用户添加一个默认权限
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities
        );
    }

    @Transactional // 开启事务
    public User registerUser(String username, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 创建新用户 (注意：我们没有设置 ID，JPA 会在保存时自动生成)
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword);

        return userRepository.save(newUser);
    }

    @Transactional // 开启事务
    public User updateUserAvatar(String username, String filePath) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("未找到用户"));
        user.setAvatarPath(filePath);

        // JPA 的 save 方法在这里会执行更新 (merge) 操作，因为它处理的是一个已有 ID 的实体
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}package com.example.textgame.service;

import com.example.textgame.model.User;
// 导入新的 UserRepository
import com.example.textgame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 导入事务注解

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    // 依赖注入已更改为 JPA Repository
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true) // 读取操作设为只读事务
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("未找到用户: " + username));

        // 为用户添加一个默认权限
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities
        );
    }

    @Transactional // 开启事务
    public User registerUser(String username, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 创建新用户 (注意：我们没有设置 ID，JPA 会在保存时自动生成)
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword);

        return userRepository.save(newUser);
    }

    @Transactional // 开启事务
    public User updateUserAvatar(String username, String filePath) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("未找到用户"));
        user.setAvatarPath(filePath);

        // JPA 的 save 方法在这里会执行更新 (merge) 操作，因为它处理的是一个已有 ID 的实体
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}package com.example.textgame.service;

import com.example.textgame.model.User;
// 导入新的 UserRepository
import com.example.textgame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 导入事务注解

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    // 依赖注入已更改为 JPA Repository
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true) // 读取操作设为只读事务
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("未找到用户: " + username));

        // 为用户添加一个默认权限
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities
        );
    }

    @Transactional // 开启事务
    public User registerUser(String username, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 创建新用户 (注意：我们没有设置 ID，JPA 会在保存时自动生成)
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword);

        return userRepository.save(newUser);
    }

    @Transactional // 开启事务
    public User updateUserAvatar(String username, String filePath) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("未找到用户"));
        user.setAvatarPath(filePath);

        // JPA 的 save 方法在这里会执行更新 (merge) 操作，因为它处理的是一个已有 ID 的实体
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}