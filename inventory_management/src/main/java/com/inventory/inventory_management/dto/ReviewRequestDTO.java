package com.inventory.inventory_management.dto;

import com.inventory.inventory_management.entity.RequestStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for REVIEWING (approving or rejecting) a request
 *
 * USAGE:
 * PUT /api/requests/{id}/review
 * Body: {
 *   "status": "APPROVED",
 *   "reviewRemarks": "Stock available, request approved."
 * }
 *
 * STATUS must be either APPROVED or REJECTED (not PENDING)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDTO {

    @NotNull(message = "Status is required")
    private RequestStatus status;

    @Size(max = 500, message = "Review remarks cannot exceed 500 characters")
    private String reviewRemarks;
}
