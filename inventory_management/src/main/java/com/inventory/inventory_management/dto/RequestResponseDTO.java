package com.inventory.inventory_management.dto;

import java.time.LocalDateTime;

import com.inventory.inventory_management.entity.RequestStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for RETURNING request data in API responses
 *
 * RESPONSE EXAMPLE:
 * {
 *   "requestId": 1,
 *   "itemId": 2,
 *   "itemName": "Blue Ballpoint Pen",
 *   "requestedQuantity": 5,
 *   "requesterName": "John Doe",
 *   "requesterEmail": "john@example.com",
 *   "status": "PENDING",
 *   "requestedAt": "2026-03-13T10:00:00",
 *   "reviewedAt": null,
 *   "reviewRemarks": null
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestResponseDTO {

    private Long requestId;
    private Long itemId;
    private String itemName;
    private Integer requestedQuantity;
    private String requesterName;
    private String requesterEmail;
    private RequestStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;
    private String reviewRemarks;
}
