package com.inventory.inventory_management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REQUEST_ITEM DTO
 * 
 * Data Transfer Object for RequestItem join table
 * Used in API requests/responses for many-to-many items in requests
 * 
 * EXAMPLE:
 * {
 *   "requestItemId": 1,
 *   "itemId": 5,
 *   "itemName": "Blue Pen",
 *   "quantity": 50
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestItemDTO {

    private Long requestItemId;

    @NotNull(message = "Item ID is required")
    private Long itemId;

    private String itemName;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
