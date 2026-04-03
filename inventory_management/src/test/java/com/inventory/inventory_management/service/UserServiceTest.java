package com.inventory.inventory_management.service;

import com.inventory.inventory_management.dto.RegisterRequestDTO;
import com.inventory.inventory_management.entity.Role;
import com.inventory.inventory_management.entity.User;
import com.inventory.inventory_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequestDTO registerDTO;
    private User user;

    @BeforeEach
    void setUp() {
        registerDTO = new RegisterRequestDTO("Alice", "alice@example.com", "Pass1234", null);

        user = new User();
        user.setUserId(1L);
        user.setFullName("Alice");
        user.setEmail("alice@example.com");
        user.setPassword("encoded-pass");
        user.setRole(Role.USER);
    }

    @Test
    void register_Success_DefaultRoleUser() {
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Pass1234")).thenReturn("encoded-pass");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.register(registerDTO);

        assertNotNull(result);
        assertEquals(Role.USER, result.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_Success_ExplicitRole() {
        registerDTO.setRole(Role.ADMIN);
        user.setRole(Role.ADMIN);

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Pass1234")).thenReturn("encoded-pass");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.register(registerDTO);

        assertEquals(Role.ADMIN, result.getRole());
    }

    @Test
    void register_DuplicateEmail_Throws() {
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.register(registerDTO));
    }

    @Test
    void getByEmail_NotFound_Throws() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getByEmail("alice@example.com"));
    }

    @Test
    void loadUserByUsername_Success() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername("alice@example.com");

        assertEquals("alice@example.com", result.getUsername());
        assertEquals("encoded-pass", result.getPassword());
        assertEquals("ROLE_USER", result.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_NotFound_Throws() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("missing@example.com"));
    }
}
