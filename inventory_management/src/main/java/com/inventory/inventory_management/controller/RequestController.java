package com.inventory.inventory_management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.inventory_management.dto.ApiResponseDTO;
import com.inventory.inventory_management.dto.RequestDTO;
import com.inventory.inventory_management.dto.RequestResponseDTO;
import com.inventory.inventory_management.dto.ReviewRequestDTO;
import com.inventory.inventory_management.entity.RequestStatus;
import com.inventory.inventory_management.service.RequestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REQUEST CONTROLLER
 *
 * BASE URL: /api/requests
 *
 * ENDPOINT LIST:
 * - POST   /api/requests                        → Submit a new request
 * - GET    /api/requests                        → Get all requests
 * - GET    /api/requests/{id}                   → Get one request
 * - GET    /api/requests/status/{status}        → Filter by status (PENDING/APPROVED/REJECTED)
 * - GET    /api/requests/item/{itemId}          → Get requests for an item
 * - GET    /api/requests/email/{email}          → Get requests by requester email
 * - PUT    /api/requests/{id}/review            → Approve or reject a request
 * - DELETE /api/requests/{id}                   → Delete a PENDING request
 */
@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    /**
     * SUBMIT a new request
     *
     * ENDPOINT: POST /api/requests
     *
     * POSTMAN TEST:
     * POST http://localhost:8080/api/requests
     * Headers: Content-Type: application/json
     * Body: {
     *   "items": [
     *     { "itemId": 1, "quantity": 5 },
     *     { "itemId": 3, "quantity": 2 }
     *   ],
     *   "requesterName": "John Doe",
     *   "requesterEmail": "john@example.com"
     * }
     *
     * RESPONSE (201 Created):
     * {
     *   "requestId": 1,
     *   "items": [
     *     { "requestItemId": 10, "itemId": 1, "itemName": "Blue Ballpoint Pen", "quantity": 5 },
     *     { "requestItemId": 11, "itemId": 3, "itemName": "Notebook", "quantity": 2 }
     *   ],
     *   "requesterName": "John Doe",
     *   "requesterEmail": "john@example.com",
     *   "status": "PENDING",
     *   "requestedAt": "2026-03-13T10:00:00",
     *   "reviewedAt": null,
     *   "reviewRemarks": null
     * }
     */
    @PostMapping
    public ResponseEntity<RequestResponseDTO> submitRequest(@Valid @RequestBody RequestDTO requestDTO) {
        RequestResponseDTO response = requestService.submitRequest(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET all requests
     *
     * ENDPOINT: GET /api/requests
     *
     * POSTMAN TEST:
     * GET http://localhost:8080/api/requests
     */
    @GetMapping
    public ResponseEntity<List<RequestResponseDTO>> getAllRequests() {
        return ResponseEntity.ok(requestService.getAllRequests());
    }

    /**
     * GET one request by ID
     *
     * ENDPOINT: GET /api/requests/{id}
     *
     * POSTMAN TEST:
     * GET http://localhost:8080/api/requests/1
     *
     * ERROR CASE:
     * - Request not found → 404: "Request not found with ID: 1"
     */
    @GetMapping("/{id}")
    public ResponseEntity<RequestResponseDTO> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.getRequestById(id));
    }

    /**
     * GET requests filtered by status
     *
     * ENDPOINT: GET /api/requests/status/{status}
     *
     * POSTMAN TEST:
     * GET http://localhost:8080/api/requests/status/PENDING
     * GET http://localhost:8080/api/requests/status/APPROVED
     * GET http://localhost:8080/api/requests/status/REJECTED
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RequestResponseDTO>> getRequestsByStatus(
            @PathVariable RequestStatus status) {
        return ResponseEntity.ok(requestService.getRequestsByStatus(status));
    }

    /**
     * GET requests for a specific item
     *
     * ENDPOINT: GET /api/requests/item/{itemId}
     *
     * POSTMAN TEST:
     * GET http://localhost:8080/api/requests/item/1
     *
     * ERROR CASE:
     * - Item not found → 404: "Item not found with ID: 1"
     */
    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<RequestResponseDTO>> getRequestsByItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(requestService.getRequestsByItem(itemId));
    }

    /**
     * GET requests by requester email
     *
     * ENDPOINT: GET /api/requests/email/{email}
     *
     * POSTMAN TEST:
     * GET http://localhost:8080/api/requests/email/john@example.com
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<List<RequestResponseDTO>> getRequestsByEmail(@PathVariable String email) {
        return ResponseEntity.ok(requestService.getRequestsByEmail(email));
    }

    /**
     * REVIEW a request (APPROVE or REJECT)
     *
     * ENDPOINT: PUT /api/requests/{id}/review
     *
     * POSTMAN TEST:
     * PUT http://localhost:8080/api/requests/1/review
     * Headers: Content-Type: application/json
     * Body: {
     *   "status": "APPROVED",
     *   "reviewRemarks": "Stock available, request approved."
     * }
     *
     * APPROVE response → stock is reduced automatically.
     *
     * ERROR CASES:
     * - Request not found      → 404
     * - Already reviewed       → 400: "Only PENDING requests can be reviewed"
     * - Insufficient stock     → 400: "Insufficient stock. Available: X, Requested: Y"
     * - status = PENDING       → 400: "Review status must be APPROVED or REJECTED"
     */
    @PutMapping("/{id}/review")
    public ResponseEntity<RequestResponseDTO> reviewRequest(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDTO reviewDTO) {
        return ResponseEntity.ok(requestService.reviewRequest(id, reviewDTO));
    }

    /**
     * DELETE a PENDING request
     *
     * ENDPOINT: DELETE /api/requests/{id}
     *
     * POSTMAN TEST:
     * DELETE http://localhost:8080/api/requests/1
     *
     * ERROR CASES:
     * - Request not found      → 404
     * - Not PENDING            → 400: "Only PENDING requests can be deleted"
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> deleteRequest(@PathVariable Long id) {
        requestService.deleteRequest(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Request deleted successfully"));
    }
}
