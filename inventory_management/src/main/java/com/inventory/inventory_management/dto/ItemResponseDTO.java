package com.inventory.inventory_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for RETURNING item data
 * 
 * CLEAN RESPONSE: Shows item info with category name (not just ID)
 * 
 * USAGE:
 * GET /items response:
 * {
 * "itemId": 1,
 * "itemName": "Blue Ballpoint Pen",
 * "description": "Smooth writing, 0.7mm",
 * "stockQuantity": 150,
 * "categoryId": 1,
 * "categoryName": "Writing Supplies"
 * }
 * 
 * WHY categoryName included:
 * Frontend can show: "Blue Pen (Writing Supplies)"
 * Without this, frontend would need to make extra API call to get category name
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDTO {

    private Long itemId;
    private String itemName;
    private String description;
    private Integer stockQuantity;
    private Long categoryId;
    private String categoryName; // User-friendly: shows category name, not just ID
}
