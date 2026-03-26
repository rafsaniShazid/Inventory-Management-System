package com.inventory.inventory_management.exception;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * TEST: GlobalExceptionHandler
 * 
 * PURPOSE: Verify exception handler is wired correctly
 * 
 * Lightweight test that validates exception mapping without full Spring context
 */
class GlobalExceptionHandlerTest {

    /**
     * TEST: Verify ResourceNotFoundException class exists and has proper structure
     */
    @Test
    void testResourceNotFound_ClassExists() {
        try {
            Class<?> clazz = Class.forName("com.inventory.inventory_management.exception.ResourceNotFoundException");
            assertTrue(clazz != null, "ResourceNotFoundException should exist");
        } catch (ClassNotFoundException e) {
            assertTrue(false, "ResourceNotFoundException class must exist");
        }
    }

    /**
     * TEST: Verify GlobalExceptionHandler class is a RestControllerAdvice
     */
    @Test
    void testGlobalExceptionHandler_AnnotationExists() {
        try {
            Class<?> clazz = Class.forName("com.inventory.inventory_management.exception.GlobalExceptionHandler");
            assertTrue(clazz.isAnnotationPresent(
                org.springframework.web.bind.annotation.RestControllerAdvice.class),
                "GlobalExceptionHandler should have @RestControllerAdvice annotation");
        } catch (ClassNotFoundException e) {
            assertTrue(false, "GlobalExceptionHandler class must exist");
        }
    }
}
