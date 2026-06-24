package com.inventory.inventory_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventory.inventory_management.entity.Request;
import com.inventory.inventory_management.entity.RequestStatus;

/**
 * REQUEST REPOSITORY
 *
 * Handles all database operations for Requests.
 *
 * FREE METHODS (from JpaRepository<Request, Long>):
 * - save(request)       → Add or update request
 * - findById(id)        → Get request by ID
 * - findAll()           → Get all requests
 * - deleteById(id)      → Delete request
 */
@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    /**
     * Find all requests by status (PENDING, APPROVED, REJECTED)
     * SQL: SELECT * FROM requests WHERE status = ?
     */
    List<Request> findByStatus(RequestStatus status);

    /**
     * Find all requests by a specific requester email
     * SQL: SELECT * FROM requests WHERE requester_email = ?
     */
    List<Request> findByRequesterEmail(String requesterEmail);
}
