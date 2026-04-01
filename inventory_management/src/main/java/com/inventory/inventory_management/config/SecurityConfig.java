package com.inventory.inventory_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * TEMPORARY SECURITY CONFIGURATION
 * 
 * PURPOSE: Allow all requests during Phase 1 testing
 * 
 * WHY NEEDED:
 * Spring Security (in your pom.xml) blocks ALL requests by default
 * Without this, you'd get 401 Unauthorized errors when testing
 * 
 * WHAT IT DOES:
 * - Disables CSRF protection (safe for testing)
 * - Permits all requests (no login needed)
 * 
 * LATER IN PHASE 3 (Your responsibility - Member 1):
 * You'll replace this with REAL security:
 * - JWT token authentication
 * - Role-based access control:
 * * ADMIN can DELETE items
 * * MANAGER can UPDATE stock
 * * USER can only VIEW items
 * - Password encryption (BCrypt)
 * 
 * FOR NOW: Just lets you test without authentication errors
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for testing
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Allow all requests (temporary!)
                );

        return http.build();
    }
}
