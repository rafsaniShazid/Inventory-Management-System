package com.inventory.inventory_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for CREATING a new request with multiple items
 *
 * USAGE:
 * POST /api/requests
 * Body: {
 *   "items": [
 *     { "itemId": 1, "quantity": 5 },
 *     { "itemId": 3, "quantity": 10 }
 *   ],
 *   "requesterName": "John Doe",
 *   "requesterEmail": "john@example.com"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {

    @NotEmpty(message = "At least one item is required in the request")
    private List<RequestItemDTO> items;

    @NotBlank(message = "Requester name is required")
    @Size(min = 2, max = 100, message = "Requester name must be between 2 and 100 characters")
    private String requesterName;

    @NotBlank(message = "Requester email is required")
    @Email(message = "Requester email must be a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String requesterEmail;
}
