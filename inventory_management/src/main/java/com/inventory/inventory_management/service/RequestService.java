package com.inventory.inventory_management.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.inventory_management.dto.RequestDTO;
import com.inventory.inventory_management.dto.RequestResponseDTO;
import com.inventory.inventory_management.dto.ReviewRequestDTO;
import com.inventory.inventory_management.entity.Item;
import com.inventory.inventory_management.entity.Request;
import com.inventory.inventory_management.entity.RequestStatus;
import com.inventory.inventory_management.exception.ResourceNotFoundException;
import com.inventory.inventory_management.repository.ItemRepository;
import com.inventory.inventory_management.repository.RequestRepository;

import lombok.RequiredArgsConstructor;

/**
 * REQUEST SERVICE
 *
 * Handles all business logic for item requests.
 *
 * WHAT THIS SERVICE DOES:
 * - Submit new requests for items
 * - Review (approve or reject) requests
 * - List requests with filters
 * - On approval, reduce item stock
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RequestService {

    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;

    /**
     * SUBMIT a new request
     *
     * BUSINESS RULES:
     * 1. Item must exist
     * 2. Requested quantity must be > 0
     * 3. Request starts as PENDING
     */
    public RequestResponseDTO submitRequest(RequestDTO requestDTO) {
        Item item = itemRepository.findById(requestDTO.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Item not found with ID: " + requestDTO.getItemId()));

        Request request = new Request();
        request.setItem(item);
        request.setRequestedQuantity(requestDTO.getRequestedQuantity());
        request.setRequesterName(requestDTO.getRequesterName());
        request.setRequesterEmail(requestDTO.getRequesterEmail());
        // status and requestedAt are set by @PrePersist in entity

        return toResponseDTO(requestRepository.save(request));
    }

    /**
     * REVIEW a request (APPROVE or REJECT)
     *
     * BUSINESS RULES:
     * 1. Request must exist
     * 2. Only PENDING requests can be reviewed
     * 3. On APPROVED: reduce item stock (must have sufficient stock)
     */
    public RequestResponseDTO reviewRequest(Long requestId, ReviewRequestDTO reviewDTO) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Request not found with ID: " + requestId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING requests can be reviewed. Current status: " + request.getStatus());
        }

        if (reviewDTO.getStatus() == RequestStatus.PENDING) {
            throw new IllegalArgumentException("Review status must be APPROVED or REJECTED");
        }

        if (reviewDTO.getStatus() == RequestStatus.APPROVED) {
            Item item = request.getItem();
            if (item.getStockQuantity() < request.getRequestedQuantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock. Available: " + item.getStockQuantity()
                        + ", Requested: " + request.getRequestedQuantity());
            }
            item.setStockQuantity(item.getStockQuantity() - request.getRequestedQuantity());
            itemRepository.save(item);
        }

        request.setStatus(reviewDTO.getStatus());
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewRemarks(reviewDTO.getReviewRemarks());

        return toResponseDTO(requestRepository.save(request));
    }

    /**
     * GET all requests
     */
    @Transactional(readOnly = true)
    public List<RequestResponseDTO> getAllRequests() {
        return requestRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET request by ID
     */
    @Transactional(readOnly = true)
    public RequestResponseDTO getRequestById(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Request not found with ID: " + requestId));
        return toResponseDTO(request);
    }

    /**
     * GET requests by status
     */
    @Transactional(readOnly = true)
    public List<RequestResponseDTO> getRequestsByStatus(RequestStatus status) {
        return requestRepository.findByStatus(status)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET requests for a specific item
     */
    @Transactional(readOnly = true)
    public List<RequestResponseDTO> getRequestsByItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("Item not found with ID: " + itemId);
        }
        return requestRepository.findByItemItemId(itemId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET requests by requester email
     */
    @Transactional(readOnly = true)
    public List<RequestResponseDTO> getRequestsByEmail(String email) {
        return requestRepository.findByRequesterEmail(email)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * DELETE a request (only PENDING requests can be deleted)
     */
    public void deleteRequest(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Request not found with ID: " + requestId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Only PENDING requests can be deleted");
        }

        requestRepository.deleteById(requestId);
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private RequestResponseDTO toResponseDTO(Request request) {
        return new RequestResponseDTO(
                request.getRequestId(),
                request.getItem().getItemId(),
                request.getItem().getItemName(),
                request.getRequestedQuantity(),
                request.getRequesterName(),
                request.getRequesterEmail(),
                request.getStatus(),
                request.getRequestedAt(),
                request.getReviewedAt(),
                request.getReviewRemarks()
        );
    }
}
