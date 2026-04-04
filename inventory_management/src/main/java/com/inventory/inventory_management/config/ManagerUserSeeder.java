package com.inventory.inventory_management.config;

import com.inventory.inventory_management.entity.Role;
import com.inventory.inventory_management.entity.User;
import com.inventory.inventory_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ManagerUserSeeder implements CommandLineRunner {

    private static final String MANAGER_EMAIL = "manager@example.com";
    private static final String MANAGER_PASSWORD = "Manager123!";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(MANAGER_EMAIL)) {
            log.info("Manager user already exists: {}", MANAGER_EMAIL);
            return;
        }

        User manager = new User();
        manager.setFullName("Inventory Manager");
        manager.setEmail(MANAGER_EMAIL);
        manager.setPassword(passwordEncoder.encode(MANAGER_PASSWORD));
        manager.setRole(Role.MANAGER);

        userRepository.save(manager);
        log.info("Seeded manager user: {}", MANAGER_EMAIL);
    }
}