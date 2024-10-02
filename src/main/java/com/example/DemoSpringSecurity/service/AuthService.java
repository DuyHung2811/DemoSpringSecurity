package com.example.DemoSpringSecurity.service;

import com.example.DemoSpringSecurity.dto.LoginDto;
import com.example.DemoSpringSecurity.dto.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);
    String register(RegisterDto registerDto);
}
