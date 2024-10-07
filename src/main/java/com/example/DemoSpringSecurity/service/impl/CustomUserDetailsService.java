package com.example.DemoSpringSecurity.service.impl;

import com.example.DemoSpringSecurity.model.User;
import com.example.DemoSpringSecurity.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not exists by Username or Email"));

        Set<GrantedAuthority> authorities = user.getRoles().stream() // Tập hợp tất cả quyền của người dùng vào một Set các đối tượng GrantedAuthority
                .map((role) -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        // Sửa lại cách in quyền hạn
        System.out.println("Quyền hạn: " + authorities);

        return new org.springframework.security.core.userdetails.User( // Tạo một đối tượng User từ Spring Security chứa các thông tin Tên người dùng hoặc email, Mật khẩu của người dùng đã được mã hóa, Danh sách các quyền (roles) của người dùng
                usernameOrEmail,
                user.getPassword(),
                authorities
        );
    }
}
