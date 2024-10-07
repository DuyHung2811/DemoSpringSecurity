package com.example.DemoSpringSecurity.service.impl;
import com.example.DemoSpringSecurity.dto.LoginDto;
import com.example.DemoSpringSecurity.dto.RegisterDto;
import com.example.DemoSpringSecurity.jwt.JwtTokenProvider;
import com.example.DemoSpringSecurity.model.Role;
import com.example.DemoSpringSecurity.model.User;
import com.example.DemoSpringSecurity.repository.RoleRepository;
import com.example.DemoSpringSecurity.repository.UserRepository;
import com.example.DemoSpringSecurity.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;

    private JwtTokenProvider jwtTokenProvider;
    private UserRepository userRepository; // Giả sử bạn có một UserRepository để lưu người dùng
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder; // Mã hóa mật khẩu

    public String login(LoginDto loginDto) {
        try {
            // Xác thực thông tin đăng nhập
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getUsernameOrEmail(),
                    loginDto.getPassword()
            ));

            // Lưu trữ thông tin xác thực này vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // In ra quyền của người dùng
            printUserAuthorities(authentication); // Gọi phương thức in ra quyền của người dùng

            // Tạo và trả về token
            return jwtTokenProvider.generateToken(authentication);
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Username or password is incorrect."); // Thông báo lỗi khi tài khoản hoặc mật khẩu không đúng
        } catch (UsernameNotFoundException e) {
            throw new RuntimeException("Username not found."); // Thông báo lỗi khi không tìm thấy tài khoản
        }
    }


    @Override
    public String register(RegisterDto registerDto) {
        // Kiểm tra xem tên người dùng hoặc email đã tồn tại chưa
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new RuntimeException("Username is already taken.");
        }
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new RuntimeException("Email is already in use.");
        }

        // Tạo người dùng mới
        User newUser = new User();
        newUser.setName(registerDto.getName());
        newUser.setUsername(registerDto.getUsername());
        newUser.setEmail(registerDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerDto.getPassword())); // Mã hóa mật khẩu

        // Gán vai trò USER cho người dùng
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found"));
        newUser.setRoles(Collections.singleton(userRole)); // Gán vai trò USER cho người dùng

        // Lưu người dùng vào cơ sở dữ liệu
        userRepository.save(newUser);

        // Bạn có thể chọn trả về token ngay lập tức hoặc trả về thông báo thành công
        return "User registered successfully!";
    }


    // Phương thức để in quyền của người dùng
    private void printUserAuthorities(Authentication authentication) {
        if (authentication != null) {
            // Lấy quyền của người dùng
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            // In ra các quyền
            authorities.forEach(authority -> System.out.println("quyền của người dùng:" + authority.getAuthority()));
        } else {
            System.out.println("No authentication found");
        }
    }
}
