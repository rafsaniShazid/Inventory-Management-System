package com.inventory.inventory_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for CREATING a new request
 *
 * USAGE:
 * POST /api/requests
 * Body: {
 *   "itemId": 1,
 *   "requestedQuantity": 5,
 *   "requesterName": "John Doe",
 *   "requesterEmail": "john@example.com"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {

    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotNull(message = "Requested quantity is required")
    @Min(value = 1, message = "Requested quantity must be at least 1")
    private Integer requestedQuantity;

    @NotBlank(message = "Requester name is required")
    @Size(min = 2, max = 100, message = "Requester name must be between 2 and 100 characters")
    private String requesterName;

    @NotBlank(message = "Requester email is required")
    @Email(message = "Requester email must be a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String requesterEmail;
}
