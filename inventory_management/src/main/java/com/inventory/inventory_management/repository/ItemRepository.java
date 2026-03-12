package com.inventory.inventory_management.repository;

import com.inventory.inventory_management.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ITEM REPOSITORY (Member 1 - YOUR MAIN MODULE!)
 * 
 * PURPOSE: Handle all database operations for Items
 * This is WHERE your data comes from!
 * 
 * FREE METHODS (from JpaRepository<Item, Long>):
 * - save(item) → Add or update item
 * - findById(id) → Get item by ID
 * - findAll() → Get all items
 * - deleteById(id) → Delete item
 * 
 * HOW TO KNOW IT'S WORKING:
 * 1. Call itemRepository.save(new Item("Pen", 100))
 * 2. Check database → New row in items table ✓
 * 3. Call itemRepository.findAll()
 * 4. See the pen in the list ✓
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Find all items in a specific category
     * 
     * SQL generated:
     * SELECT * FROM items WHERE category_id = ?
     * 
     * USAGE in Controller:
     * GET /items?category=1 → Shows all items in category 1
     * 
     * WHY NEEDED: Users want to browse by category
     * "Show me all Writing Supplies" → filters items
     */
    List<Item> findByCategoryCategoryId(Long categoryId);

    /**
     * Search items by name (case-insensitive, partial match)
     * 
     * SQL generated:
     * SELECT * FROM items WHERE LOWER(item_name) LIKE LOWER('%searchTerm%')
     * 
     * USAGE:
     * Search "pen" → finds "Blue Pen", "Pen Set", "Pencil"
     * 
     * WHY NEEDED: Users need search functionality
     * "I want to find notebooks" → search box
     */
    List<Item> findByItemNameContainingIgnoreCase(String itemName);

    /**
     * Find items with low stock (less than specified quantity)
     * 
     * SQL generated:
     * SELECT * FROM items WHERE stock_quantity < ?
     * 
     * USAGE:
     * itemRepository.findByStockQuantityLessThan(10)
     * → Shows items with less than 10 in stock
     * 
     * WHY NEEDED: Alert admins to reorder items
     * "These items are running low!"
     */
    List<Item> findByStockQuantityLessThan(Integer quantity);

    /**
     * Get items with stock greater than or equal to quantity
     * 
     * USAGE: Find items that are well-stocked
     */
    List<Item> findByStockQuantityGreaterThanEqual(Integer quantity);

    /**
     * Check if item exists by name
     * Useful for validation before creating
     */
    boolean existsByItemName(String itemName);

    /**
     * Custom query example - Get total number of items in stock
     * 
     * @Query lets you write custom SQL/JPQL
     * 
     *        This calculates: SUM of all stock quantities
     *        Example:
     *        - Pen: 100
     *        - Pencil: 50
     *        - Total: 150
     */
    @Query("SELECT SUM(i.stockQuantity) FROM Item i")
    Long getTotalStockCount();

    /**
     * Get items by category name (instead of category ID)
     * Useful for user-friendly endpoints
     * 
     * JPQL joins Item → Category
     * SQL: SELECT i.* FROM items i
     * JOIN categories c ON i.category_id = c.category_id
     * WHERE c.category_name = ?
     */
    @Query("SELECT i FROM Item i WHERE i.category.categoryName = :categoryName")
    List<Item> findByCategoryName(@Param("categoryName") String categoryName);
}
