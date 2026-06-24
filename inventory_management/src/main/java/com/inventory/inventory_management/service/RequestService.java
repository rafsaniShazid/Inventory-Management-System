package com.inventory.inventory_management.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.inventory_management.dto.DtoMapper;
import com.inventory.inventory_management.dto.RequestDTO;
import com.inventory.inventory_management.dto.RequestItemDTO;
import com.inventory.inventory_management.dto.RequestResponseDTO;
import com.inventory.inventory_management.dto.ReviewRequestDTO;
import com.inventory.inventory_management.entity.Item;
import com.inventory.inventory_management.entity.Request;
import com.inventory.inventory_management.entity.RequestItem;
import com.inventory.inventory_management.entity.RequestStatus;
import com.inventory.inventory_management.exception.ResourceNotFoundException;
import com.inventory.inventory_management.repository.ItemRepository;
import com.inventory.inventory_management.repository.RequestItemRepository;
import com.inventory.inventory_management.repository.RequestRepository;

import lombok.RequiredArgsConstructor;

/**
 * REQUEST SERVICE
 *
 * Handles all business logic for item requests with many-to-many relationship.
 *
 * WHAT THIS SERVICE DOES:
 * - Submit new requests for multiple items
 * - Review (approve or reject) requests
 * - List requests with filters
 * - On approval, reduce item stock for all items in the request
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RequestService {

    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final DtoMapper dtoMapper;
    private final RequestItemRepository requestItemRepository;

    /**
     * SUBMIT a new request
     *
     * BUSINESS RULES:
        * 1. Request must contain at least one item
        * 2. All items must exist
        * 3. Requested quantities must be > 0
        * 4. Request starts as PENDING
     */
    public RequestResponseDTO submitRequest(RequestDTO requestDTO) {
        Request request = new Request();
        request.setRequesterName(requestDTO.getRequesterName());
        request.setRequesterEmail(requestDTO.getRequesterEmail());
        // status and requestedAt are set by @PrePersist in entity
        // Validate items list is not empty
        if (requestDTO.getItems() == null || requestDTO.getItems().isEmpty()) {
            throw new IllegalArgumentException("Request must contain at least one item");
        }

        // Create RequestItem for each item in the request
        for (RequestItemDTO itemDTO : requestDTO.getItems()) {
            Long itemId = Objects.requireNonNull(itemDTO.getItemId(), "Item ID is required");
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                    "Item not found with ID: " + itemId));

            RequestItem requestItem = new RequestItem();
            requestItem.setRequest(request);
            requestItem.setItem(item);
            requestItem.setQuantity(itemDTO.getQuantity());
            request.getItems().add(requestItem);
        }

        return dtoMapper.toRequestResponseDTO(requestRepository.save(request));
    }

    /**
     * REVIEW a request (APPROVE or REJECT)
     *
     * BUSINESS RULES:
     * 1. Request must exist
     * 2. Only PENDING requests can be reviewed
        * 3. On APPROVED: reduce item stock for all items (must have sufficient stock)
     */
    public RequestResponseDTO reviewRequest(Long requestId, ReviewRequestDTO reviewDTO) {
        Long safeRequestId = Objects.requireNonNull(requestId, "Request ID is required");
        Request request = requestRepository.findById(safeRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                "Request not found with ID: " + safeRequestId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING requests can be reviewed. Current status: " + request.getStatus());
        }

        if (reviewDTO.getStatus() == RequestStatus.PENDING) {
            throw new IllegalArgumentException("Review status must be APPROVED or REJECTED");
        }

        if (reviewDTO.getStatus() == RequestStatus.APPROVED) {
            // Check stock availability for all items
            for (RequestItem requestItem : request.getItems()) {
                Item item = requestItem.getItem();
                if (item.getStockQuantity() < requestItem.getQuantity()) {
                    throw new IllegalArgumentException(
                            "Insufficient stock for item '" + item.getItemName() + "'. "
                            + "Available: " + item.getStockQuantity()
                            + ", Requested: " + requestItem.getQuantity());
                }
            }

            // Reduce stock for all approved items
            for (RequestItem requestItem : request.getItems()) {
                Item item = requestItem.getItem();
                item.setStockQuantity(item.getStockQuantity() - requestItem.getQuantity());
                itemRepository.save(item);
            }
        }

        request.setStatus(reviewDTO.getStatus());
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewRemarks(reviewDTO.getReviewRemarks());

        return dtoMapper.toRequestResponseDTO(requestRepository.save(request));
    }

    /**
     * GET all requests
     */
    @Transactional(readOnly = true)
    public List<RequestResponseDTO> getAllRequests() {
        return requestRepository.findAll()
                .stream()
                .map(dtoMapper::toRequestResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET request by ID
     */
    @Transactional(readOnly = true)
    public RequestResponseDTO getRequestById(Long requestId) {
        Long safeRequestId = Objects.requireNonNull(requestId, "Request ID is required");
        Request request = requestRepository.findById(safeRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                "Request not found with ID: " + safeRequestId));
        return dtoMapper.toRequestResponseDTO(request);
    }

    /**
     * GET requests by status
     */
    @Transactional(readOnly = true)
    public List<RequestResponseDTO> getRequestsByStatus(RequestStatus status) {
        return requestRepository.findByStatus(status)
                .stream()
                .map(dtoMapper::toRequestResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET requests for a specific item
    * Returns all requests that contain the specified item
     */
    @Transactional(readOnly = true)
    public List<RequestResponseDTO> getRequestsByItem(Long itemId) {
        Long safeItemId = Objects.requireNonNull(itemId, "Item ID is required");
        if (!itemRepository.existsById(safeItemId)) {
            throw new ResourceNotFoundException("Item not found with ID: " + safeItemId);
        }
        return requestItemRepository.findByItemItemId(safeItemId)
                .stream()
                .map(requestItem -> requestItem.getRequest())
                .distinct()
                .map(dtoMapper::toRequestResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET requests by requester email
     */
    @Transactional(readOnly = true)
    public List<RequestResponseDTO> getRequestsByEmail(String email) {
        return requestRepository.findByRequesterEmail(email)
                .stream()
                .map(dtoMapper::toRequestResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * DELETE a request (only PENDING requests can be deleted)
     */
    public void deleteRequest(Long requestId) {
        Long safeRequestId = Objects.requireNonNull(requestId, "Request ID is required");
        Request request = requestRepository.findById(safeRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                "Request not found with ID: " + safeRequestId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Only PENDING requests can be deleted");
        }

        requestRepository.deleteById(safeRequestId);
    }

    // ─── Helper ──────────────────────────────────────────────────────────────
}

