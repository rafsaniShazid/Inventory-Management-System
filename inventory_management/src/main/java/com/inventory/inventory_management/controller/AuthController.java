package com.inventory.inventory_management.controller;

import com.inventory.inventory_management.config.JwtUtil;
import com.inventory.inventory_management.dto.AuthResponseDTO;
import com.inventory.inventory_management.dto.LoginRequestDTO;
import com.inventory.inventory_management.dto.RegisterRequestDTO;
import com.inventory.inventory_management.entity.User;
import com.inventory.inventory_management.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO requestDTO) {
        User createdUser = userService.register(requestDTO);

        UserDetails userDetails = userService.loadUserByUsername(createdUser.getEmail());
        String token = jwtUtil.generateToken(userDetails, createdUser.getRole().name());

        AuthResponseDTO response = new AuthResponseDTO(
                token,
                "Bearer",
                createdUser.getEmail(),
                createdUser.getRole());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.getEmail(), requestDTO.getPassword()));

        User user = userService.getByEmail(requestDTO.getEmail());
        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails, user.getRole().name());

        AuthResponseDTO response = new AuthResponseDTO(
                token,
                "Bearer",
                user.getEmail(),
                user.getRole());

        return ResponseEntity.ok(response);
    }
}
