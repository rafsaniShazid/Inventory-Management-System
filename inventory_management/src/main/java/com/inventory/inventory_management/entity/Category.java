package com.inventory.inventory_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * CATEGORY ENTITY - YOUR MODULE (Member 1)!
 * 
 * DATABASE TABLE: categories
 * 
 * PURPOSE: Organize stationery items into groups
 * Makes it easier for users to browse and find items
 * 
 * EXAMPLES:
 * - "Writing Supplies" → pens, pencils, markers
 * - "Paper Products" → notebooks, printer paper, sticky notes
 * - "Office Equipment" → staplers, scissors, tape
 * - "Art Supplies" → colors, sketch pens, drawing sheets
 * 
 * WHY NEEDED:
 * Instead of showing 200 items in one list, users can:
 * - Click "Writing Supplies" → see only pens, pencils
 * - Click "Paper Products" → see only paper items
 * 
 * YOUR RESPONSIBILITY:
 * Later you'll create:
 * - CategoryController: API endpoints (GET /categories, POST /categories)
 * - CategoryService: Business logic
 * - CategoryRepository: Database access
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(unique = true, nullable = false, length = 100)
    private String categoryName; // "Writing Supplies", "Paper Products"

    @Column(length = 500)
    private String description; // Optional: "All writing instruments..."

    /**
     * ONE CATEGORY → MANY ITEMS
     * One category contains multiple items
     * 
     * Example:
     * Category: "Writing Supplies"
     * ├── Item: "Blue Pen"
     * ├── Item: "Black Pen"
     * ├── Item: "Pencil"
     * └── Item: "Eraser"
     */
    @OneToMany(mappedBy = "category")
    private Set<Item> items;
}
