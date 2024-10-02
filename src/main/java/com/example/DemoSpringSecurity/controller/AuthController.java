package com.example.DemoSpringSecurity.controller;

import com.example.DemoSpringSecurity.dto.*;
import com.example.DemoSpringSecurity.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody LoginDto loginDto) {
        try {
            String token = authService.login(loginDto);
            JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
            jwtAuthResponse.setAccessToken(token);

            // Tạo phản hồi cho trường hợp thành công
            ApiResponse<Object> apiResponse = new ApiResponse<>(jwtAuthResponse, 200, "Login successful");
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Tạo phản hồi cho trường hợp thất bại
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            ApiResponse<Object> apiResponse = new ApiResponse<>(errorResponse, 401, e.getMessage());
            return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED); // Trả về mã 401 cho thông tin đăng nhập không hợp lệ
        }
    }



    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody RegisterDto registerDto) {
        String message = authService.register(registerDto); // Gọi phương thức register và nhận thông báo

        ApiResponse<String> apiResponse = new ApiResponse<>(null, 201, "Registration successful");

        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }
}
