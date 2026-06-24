package com.inventory.inventory_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventory.inventory_management.entity.RequestItem;

/**
 * REQUEST_ITEM REPOSITORY
 * 
 * Data access layer for RequestItem join table operations
 * Provides CRUD and custom queries for many-to-many relationships
 */
@Repository
public interface RequestItemRepository extends JpaRepository<RequestItem, Long> {

    /**
     * Find all request items for a specific request
     * Used when loading all items in a request
     */
    List<RequestItem> findByRequestRequestId(Long requestId);

    /**
     * Find all request items for a specific item
     * Used when checking which requests contain a specific item
     */
    List<RequestItem> findByItemItemId(Long itemId);

    /**
     * Find a specific request item
     * Used when updating or deleting a single item from a request
     */
    RequestItem findByRequestRequestIdAndItemItemId(Long requestId, Long itemId);
}
