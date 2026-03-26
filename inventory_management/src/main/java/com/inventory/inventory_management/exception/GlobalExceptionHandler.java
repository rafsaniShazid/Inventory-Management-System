package com.inventory.inventory_management.exception;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.inventory.inventory_management.dto.ApiResponseDTO;

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

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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
      * Returns 400 Bad Request with field-specific errors inside ApiResponseDTO.data
     * 
     * EXAMPLE when user sends invalid data:
     * POST /items with body: {"itemName": "", "stockQuantity": -5}
     * 
     * Response:
     * 400 Bad Request
     * {
     * "success": false,
     * "message": "Validation failed",
     * "data": {
     *   "itemName": "Item name is required",
     *   "stockQuantity": "Stock quantity cannot be negative"
     * }
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Extract all errors (both field-level and object-level)
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                errors.put(fieldError.getField(), error.getDefaultMessage());
            } else if (error instanceof ObjectError) {
                // Handle class-level constraints
                errors.put(error.getObjectName(), error.getDefaultMessage());
            } else {
                errors.put("validation", error.getDefaultMessage());
            }
        });

        ApiResponseDTO response = ApiResponseDTO.error("Validation failed");
        response.setData(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle malformed JSON in request body
     * Returns 400 Bad Request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();

        if (cause instanceof InvalidFormatException invalidFormatException
                && invalidFormatException.getTargetType() != null
                && invalidFormatException.getTargetType().isEnum()) {
            return buildEnumParseErrorResponse(invalidFormatException);
        }

        String message = (cause instanceof JsonParseException)
                ? "Malformed JSON: please check syntax (quotes, commas, brackets)."
                : "Malformed request body: unable to read JSON payload.";

        ApiResponseDTO response = ApiResponseDTO.error(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle enum parse errors from request body
     * Returns 400 Bad Request with allowed enum values
     */
    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ApiResponseDTO> handleInvalidFormat(InvalidFormatException ex) {
        if (ex.getTargetType() != null && ex.getTargetType().isEnum()) {
            return buildEnumParseErrorResponse(ex);
        }

        ApiResponseDTO response = ApiResponseDTO.error("Invalid value format in request body.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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
     * Handle IllegalStateException
     * Used for invalid operation state (e.g., reviewing non-PENDING request)
     * Returns 400 Bad Request
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseDTO> handleIllegalState(IllegalStateException ex) {
        ApiResponseDTO response = ApiResponseDTO.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle path/query parameter type mismatch (e.g., invalid enum in URL)
     * Returns 400 Bad Request
     * 
     * EXAMPLE:
     * GET /api/requests/status/INVALID_STATUS
     * Response: Invalid value for 'status'. Allowed values: [PENDING, APPROVED, REJECTED]
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseDTO> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();

        if (requiredType != null && requiredType.isEnum()) {
            String allowedValues = Arrays.stream(requiredType.getEnumConstants())
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));

            String message = "Invalid value for '" + ex.getName() + "'. Allowed values: [" + allowedValues + "]";

            Map<String, Object> details = new HashMap<>();
            details.put("parameter", ex.getName());
            details.put("invalidValue", ex.getValue());
            details.put("allowedValues", Arrays.stream(requiredType.getEnumConstants())
                    .map(String::valueOf)
                    .collect(Collectors.toList()));

            ApiResponseDTO response = ApiResponseDTO.error(message);
            response.setData(details);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        ApiResponseDTO response = ApiResponseDTO.error("Invalid value for parameter '" + ex.getName() + "'.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle all other exceptions
     * Returns 500 Internal Server Error
     * 
     * Catch-all for unexpected errors
     * Logs detailed error server-side but returns generic message to client
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO> handleGenericException(Exception ex) {
        logger.error("Unexpected exception occurred", ex);
        ApiResponseDTO response = ApiResponseDTO.error("An unexpected error occurred. Please try again later.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

        private ResponseEntity<ApiResponseDTO> buildEnumParseErrorResponse(InvalidFormatException ex) {
        String fieldPath = ex.getPath().stream()
            .map(Reference::getFieldName)
            .filter(fieldName -> fieldName != null && !fieldName.isBlank())
            .collect(Collectors.joining("."));

        String allowedValues = Arrays.stream(ex.getTargetType().getEnumConstants())
            .map(String::valueOf)
            .collect(Collectors.joining(", "));

        String message = fieldPath.isBlank()
            ? "Invalid enum value in request body. Allowed values: [" + allowedValues + "]"
            : "Invalid value for '" + fieldPath + "'. Allowed values: [" + allowedValues + "]";

        Map<String, Object> details = new HashMap<>();
        details.put("field", fieldPath.isBlank() ? null : fieldPath);
        details.put("invalidValue", ex.getValue());
        details.put("allowedValues", Arrays.stream(ex.getTargetType().getEnumConstants())
            .map(String::valueOf)
            .collect(Collectors.toList()));

        ApiResponseDTO response = ApiResponseDTO.error(message);
        response.setData(details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
}
