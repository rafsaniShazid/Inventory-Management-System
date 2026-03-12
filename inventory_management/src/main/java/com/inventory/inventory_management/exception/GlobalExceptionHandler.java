package com.inventory.inventory_management.exception;

import com.inventory.inventory_management.dto.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * GLOBAL EXCEPTION HANDLER
 * 
 * PURPOSE: Catch exceptions and return clean JSON error messages
 * Instead of ugly stack traces, users get nice error messages!
 * 
 * HOW IT WORKS:
 * 1. Exception occurs anywhere in application
 * 2. This handler catches it
 * 3. Returns clean JSON response with proper HTTP status
 * 
 * EXAMPLE:
 * Without handler:
 * 500 Internal Server Error
 * java.lang.NullPointerException at line 45...
 * (Ugly and confusing!)
 * 
 * With handler:
 * 404 Not Found
 * {
 * "success": false,
 * "message": "Item not found with ID: 999"
 * }
 * (Clean and user-friendly!)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle ResourceNotFoundException
     * Returns 404 Not Found status
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiResponseDTO response = ApiResponseDTO.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle validation errors (from @Valid annotations in DTOs)
     * Returns 400 Bad Request with field-specific errors
     * 
     * EXAMPLE when user sends invalid data:
     * POST /items with body: {"itemName": "", "stockQuantity": -5}
     * 
     * Response:
     * 400 Bad Request
     * {
     * "itemName": "Item name is required",
     * "stockQuantity": "Stock quantity cannot be negative"
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Extract all field errors
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handle IllegalArgumentException
     * Used for business rule violations
     * Returns 400 Bad Request
     * 
     * EXAMPLE:
     * throw new IllegalArgumentException("Category name already exists");
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {
        ApiResponseDTO response = ApiResponseDTO.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle all other exceptions
     * Returns 500 Internal Server Error
     * 
     * Catch-all for unexpected errors
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO> handleGenericException(Exception ex) {
        ApiResponseDTO response = ApiResponseDTO.error("An error occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
