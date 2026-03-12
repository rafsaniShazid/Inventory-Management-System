package com.inventory.inventory_management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for CREATING new item
 * 
 * SECURITY: No itemId field (prevents user from setting their own ID)
 * 
 * USAGE:
 * POST /items
 * Body: {
 * "itemName": "Blue Ballpoint Pen",
 * "description": "Smooth writing, 0.7mm tip",
 * "stockQuantity": 150,
 * "categoryId": 1
 * }
 * 
 * VALIDATION:
 * - All fields required except description
 * - Stock must be >= 0 (can't have negative inventory!)
 * - categoryId must exist in database (checked in service layer)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {

    /**
     * Item name: Required, 3-200 characters
     */
    @NotBlank(message = "Item name is required")
    @Size(min = 3, max = 200, message = "Item name must be between 3 and 200 characters")
    private String itemName;

    /**
     * Description: Optional
     * Good practice to have details about the item
     */
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    /**
     * Stock quantity: Required, must be >= 0
     * 
     * @NotNull: Cannot be null
     *           @Min(0): Must be 0 or positive
     * 
     *           BUSINESS RULE: Can't have -50 pens in stock!
     */
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    /**
     * Category ID: Required
     * Item must belong to a category
     * 
     * Service layer will verify this category exists in database
     */
    @NotNull(message = "Category ID is required")
    private Long categoryId;
}
