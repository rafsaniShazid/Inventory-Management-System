package com.inventory.inventory_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for RETURNING category data
 * 
 * WHY SEPARATE:
 * - Includes categoryId (only for responses, not creation)
 * - Clean response format
 * - Can add computed fields (like itemCount)
 * 
 * USAGE:
 * GET /categories response:
 * {
 * "categoryId": 1,
 * "categoryName": "Writing Supplies",
 * "description": "Pens, pencils",
 * "itemCount": 15
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {

    private Long categoryId;
    private String categoryName;
    private String description;
    private Integer itemCount; // How many items in this category

    /**
     * Constructor without itemCount (for simple responses)
     */
    public CategoryResponseDTO(Long categoryId, String categoryName, String description) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
    }
}
