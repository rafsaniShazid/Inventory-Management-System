package com.inventory.inventory_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REQUEST_ITEM ENTITY - JOIN TABLE FOR MANY-TO-MANY RELATIONSHIP
 * 
 * DATABASE TABLE: request_items (join table)
 * 
 * PURPOSE: Links requests to items with quantities
 * Enables a request to contain multiple items (many-to-many relationship)
 * 
 * STRUCTURE:
 * One Request → Many RequestItems
 * One Item → Many RequestItems
 * Many Requests → Many Items (through RequestItem)
 * 
 * BUSINESS LOGIC:
 * - Each RequestItem represents a specific item in a specific request
 * - Contains the quantity of that item requested
 * - Tracks which item belongs to which request
 * 
 * EXAMPLE:
 * Request #101 contains:
 *   - RequestItem 1: Item (Blue Pen) × 50 units
 *   - RequestItem 2: Item (Notebook) × 20 units
 */
@Entity
@Table(name = "request_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestItemId;

    /**
     * MANY REQUEST_ITEMS → ONE REQUEST
     * Links back to the parent request
     */
    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    /**
     * MANY REQUEST_ITEMS → ONE ITEM
     * Specifies which item is part of this request
     */
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /**
     * HOW MANY of this item are requested?
     * Business Rules:
     * - Must be > 0
     * - Cannot exceed available stock at request time
     * - Used to update inventory when request is approved
     */
    @Column(nullable = false)
    private Integer quantity;
}
