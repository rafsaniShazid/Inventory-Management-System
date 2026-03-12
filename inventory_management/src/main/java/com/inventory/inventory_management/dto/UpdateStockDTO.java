package com.inventory.inventory_management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for UPDATING only stock quantity
 * 
 * WHY SEPARATE from ItemDTO:
 * - Sometimes you only want to update stock, not name/description
 * - Simpler API: PUT /items/{id}/stock instead of full update
 * - Used when manager receives stock delivery
 * 
 * USAGE:
 * PUT /items/1/stock
 * Body: {
 * "stockQuantity": 200
 * }
 * 
 * SCENARIO:
 * 1. Current stock: 50 pens
 * 2. New delivery: +150 pens
 * 3. Send request with stockQuantity: 200
 * 4. Stock updated to 200
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStockDTO {

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;
}
