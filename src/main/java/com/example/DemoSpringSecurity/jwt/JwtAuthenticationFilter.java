package com.example.DemoSpringSecurity.jwt;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;
    private UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try{
            // Lấy URI của yêu cầu
//        String requestURI = request.getRequestURI();

            // Kiểm tra nếu yêu cầu là tới login hoặc register
//        if (requestURI.equals("/api/auth/login") || requestURI.equals("/api/auth/register")) {
//            filterChain.doFilter(request, response); // Bỏ qua filter và chuyển tiếp
//            return; // Kết thúc phương thức
//        }

            // Get JWT token from HTTP request
            String token = getTokenFromRequest(request); // Lấy JWT từ header Authorization của yêu cầu

            // Validate Token
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) { // Nếu token tồn tại và hợp lệ
                // Get username from token
                String username = jwtTokenProvider.getUsername(token); // Lấy username từ token JWT
                System.out.println("Valid token. Username: " + username);
                // Get expiration date from token
                Date expirationDate = jwtTokenProvider.getExpirationDate(token); // Lấy giá trị exp từ token
                System.out.println("Token expiration (exp): " + expirationDate); // In ra giá trị exp

                UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Tải thông tin chi tiết của người dùng dựa trên tên người dùng

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}

