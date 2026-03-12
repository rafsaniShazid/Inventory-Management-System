package com.inventory.inventory_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic response DTO for success/error messages
 * 
 * USAGE: Return simple messages from API
 * 
 * SUCCESS example:
 * {
 * "success": true,
 * "message": "Item created successfully",
 * "data": null
 * }
 * 
 * ERROR example:
 * {
 * "success": false,
 * "message": "Item not found",
 * "data": null
 * }
 * 
 * WITH DATA example:
 * {
 * "success": true,
 * "message": "Item retrieved",
 * "data": { item details... }
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDTO {

    private boolean success;
    private String message;
    private Object data; // Can hold any type of data

    /**
     * Constructor for responses without data
     */
    public ApiResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }

    /**
     * Static helper methods for cleaner code
     */
    public static ApiResponseDTO success(String message) {
        return new ApiResponseDTO(true, message, null);
    }

    public static ApiResponseDTO success(String message, Object data) {
        return new ApiResponseDTO(true, message, data);
    }

    public static ApiResponseDTO error(String message) {
        return new ApiResponseDTO(false, message, null);
    }
}
