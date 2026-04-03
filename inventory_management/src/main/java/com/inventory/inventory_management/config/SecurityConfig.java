package com.inventory.inventory_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/", "/login", "/dashboard", "/request", "/my-requests", "/items", "/categories").permitAll()
                    .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/error").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/categories/**")
                        .hasAnyRole("USER", "MANAGER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/items/**")
                        .hasAnyRole("USER", "MANAGER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/requests/**")
                        .hasAnyRole("USER", "MANAGER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/requests/email/**")
                        .hasAnyRole("USER", "MANAGER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/requests/**")
                        .hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/requests/*/review")
                        .hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/requests/**")
                        .hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/items/**",
                                "/api/categories/**")
                        .hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/items/*/stock")
                        .hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/items/**", "/api/categories/**")
                        .hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/items/**",
                                "/api/categories/**")
                        .hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
