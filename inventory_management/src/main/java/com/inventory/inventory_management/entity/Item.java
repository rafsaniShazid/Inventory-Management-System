package com.inventory.inventory_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * ITEM ENTITY - YOUR MAIN MODULE (Member 1)!
 * 
 * DATABASE TABLE: items
 * 
 * PURPOSE: Represent individual stationery items in inventory
 * This is the CORE of your module - all your work revolves around items!
 * 
 * EXAMPLES:
 * Item 1:
 * - itemName: "Blue Ballpoint Pen"
 * - description: "Smooth writing, 0.7mm tip"
 * - stockQuantity: 150
 * - category: "Writing Supplies"
 * 
 * Item 2:
 * - itemName: "A4 Notebook - 200 pages"
 * - description: "Ruled pages, hard cover"
 * - stockQuantity: 45
 * - category: "Paper Products"
 * 
 * YOUR RESPONSIBILITIES (What you'll build):
 * 1. ItemController: Create API endpoints
 * - GET /items → Show all items
 * - POST /items → Add new item (ADMIN only)
 * - PUT /items/{id} → Update item (ADMIN/MANAGER)
 * - DELETE /items/{id} → Delete item (ADMIN only)
 * 
 * 2. ItemService: Business logic
 * - Validate stock quantity (can't be negative!)
 * - Check if item exists before updating
 * - Handle stock updates when requests approved
 * 
 * 3. ItemRepository: Database operations
 * - Find items by category
 * - Search items by name
 * - Get items with low stock (< 10)
 * 
 * HOW IT'S RELATED TO PROJECT:
 * - Users browse items (view stock)
 * - Users request items (Member 2's Request module uses this)
 * - Admins manage items (your controllers)
 * - When request approved → stock quantity decreases
 */
@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(nullable = false, length = 200)
    private String itemName; // "Blue Ballpoint Pen"

    @Column(length = 500)
    private String description; // Optional: "Smooth writing, 0.7mm tip"

    /**
     * HOW MANY items are currently available?
     * 
     * IMPORTANT BUSINESS RULES (you'll enforce in ItemService):
     * - Must be >= 0 (can't have negative stock!)
     * - When user requests 5 pens → stock decreases by 5
     * - When stock < 10 → show "Low Stock" warning (future feature)
     * - When stock = 0 → mark as "Out of Stock"
     * 
     * HOW TO KNOW IF IT'S WORKING:
     * 1. Add item with stock = 50
     * 2. User requests 10 → stock becomes 40
     * 3. Check database → stockQuantity = 40 ✓
     */
    @Column(nullable = false)
    private Integer stockQuantity;

    /**
     * MANY ITEMS → ONE CATEGORY
     * Each item belongs to exactly one category
     * 
     * @JoinColumn creates foreign key "category_id" in items table
     * 
     *             Example database relationship:
     *             items table:
     *             | item_id | item_name | stock | category_id |
     *             |---------|------------|-------|-------------|
     *             | 1 | Blue Pen | 150 | 1 |
     *             | 2 | Black Pen | 200 | 1 |
     *             | 3 | Notebook | 45 | 2 |
     * 
     *             categories table:
     *             | category_id | category_name |
     *             |-------------|-------------------|
     *             | 1 | Writing Supplies |
     *             | 2 | Paper Products |
     */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * MANY-TO-MANY: ONE ITEM → MANY REQUEST_ITEMS → MANY REQUESTS
     * 
     * An item can be requested by many requests through the RequestItem join table
     * mappedBy = "item" means RequestItem owns the relationship
     */
    @OneToMany(mappedBy = "item")
    private List<RequestItem> requestItems = new ArrayList<>();

}
