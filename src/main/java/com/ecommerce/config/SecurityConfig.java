package com.ecommerce.config;

import com.ecommerce.security.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/**",
                    "/api/products/**",
                    // Swagger/OpenAPI endpoints:
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/swagger-resources/**"
                ).permitAll()
                .requestMatchers("/api/admin/orders/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/products/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/users/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/categories/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            // CORS configuration for development: allow all origins, methods, and headers for /api/**
            // WARNING: In production, restrict allowed origins, methods, and headers as appropriate!
            .cors(cors -> cors.configurationSource(request -> {
                org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
                config.setAllowedOrigins(java.util.List.of("*"));
                config.setAllowedMethods(java.util.List.of("*"));
                config.setAllowedHeaders(java.util.List.of("*"));
                config.setAllowCredentials(false);
                config.setMaxAge(3600L);
                // Only apply to /api/** endpoints
                if (request.getRequestURI().startsWith("/api/")) {
                    return config;
                }
                return null;
            }));
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}