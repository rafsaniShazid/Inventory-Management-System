package com.inventory.inventory_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for CREATING new category
 * 
 * WHY SEPARATE FROM ENTITY:
 * - No categoryId field (database generates it automatically)
 * - Validates input before saving to database
 * - User can't manipulate ID or other sensitive fields
 * 
 * USAGE in Controller:
 * POST /categories
 * Body: {
 * "categoryName": "Writing Supplies",
 * "description": "Pens, pencils, markers"
 * }
 * 
 * VALIDATION RULES:
 * - categoryName: Required, 3-100 characters
 * - description: Optional
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    /**
     * @NotBlank: Cannot be null, empty, or just whitespace
     * @Size: Length must be between 3 and 100 characters
     * 
     *        If validation fails → Automatic 400 Bad Request error
     *        Example invalid: "" → Error: "Category name is required"
     */
    @NotBlank(message = "Category name is required")
    @Size(min = 3, max = 100, message = "Category name must be between 3 and 100 characters")
    private String categoryName;

    /**
     * Description is optional (no@NotBlank)
     * Can be null or empty
     */
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}
