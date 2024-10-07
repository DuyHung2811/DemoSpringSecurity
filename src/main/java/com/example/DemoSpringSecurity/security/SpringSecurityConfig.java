package com.example.DemoSpringSecurity.security;

import com.example.DemoSpringSecurity.jwt.JwtAuthenticationEntryPoint;
import com.example.DemoSpringSecurity.jwt.JwtAuthenticationFilter;
import com.example.DemoSpringSecurity.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
@AllArgsConstructor
/*@EnableGlobalMethodSecurity(prePostEnabled = true)*/
public class SpringSecurityConfig {

    private UserDetailsService userDetailsService;

    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    private JwtAuthenticationFilter authenticationFilter;

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authorize) -> {
                    // Các endpoint yêu cầu quyền ADMIN
//                    authorize.requestMatchers("/api/admin/**").hasRole("ADMIN");

                    // Các endpoint yêu cầu quyền USER
//                    authorize.requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN");

                    // Các endpoint đc truy cập mà ko cần xác thực
                    authorize.requestMatchers("/api/auth/**").permitAll();

                    // Cho phép tất cả các yêu cầu OPTIONS (cho CORS)
                    authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

                    // Các yêu cầu còn lại phải được xác thực
                    authorize.anyRequest().authenticated();
                })
                .httpBasic(Customizer.withDefaults());

        // Xử lý lỗi xác thực
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)  // Xử lý lỗi 401
                .accessDeniedHandler((request, response, accessDeniedException) -> {  // Xử lý lỗi 403
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Access Denied!");
                })
        );

        // Thêm filter cho JWT hoặc xác thực khác nếu cần
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
