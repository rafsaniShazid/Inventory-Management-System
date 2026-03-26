package com.inventory.inventory_management.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.inventory_management.dto.DtoMapper;
import com.inventory.inventory_management.dto.ItemDTO;
import com.inventory.inventory_management.dto.ItemResponseDTO;
import com.inventory.inventory_management.dto.UpdateStockDTO;
import com.inventory.inventory_management.entity.Category;
import com.inventory.inventory_management.entity.Item;
import com.inventory.inventory_management.exception.ResourceNotFoundException;
import com.inventory.inventory_management.repository.CategoryRepository;
import com.inventory.inventory_management.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

/**
 * ITEM SERVICE (Member 1 - YOUR MAIN MODULE!)
 * 
 * This is the HEART of your inventory system!
 * All item management logic lives here.
 * 
 * WHAT THIS SERVICE DOES:
 * - Create, read, update, delete items
 * - Manage stock quantities
 * - Validate business rules
 * - Search and filter items
 * 
 * HOW IT RELATES TO PROJECT:
 * - Admins use this to manage inventory
 * - Users view items through this
 * - Request module (Member 2) calls this to reduce stock
 * - Reports use this to show inventory statistics
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final DtoMapper dtoMapper;

    /**
     * CREATE new item
     * 
     * BUSINESS RULES:
     * 1. Category must exist
     * 2. Item name should be unique (optional enforcement)
     * 3. Stock quantity must be >= 0
     * 
     * FLOW:
     * 1. Validate category exists
     * 2. Create item entity
     * 3. Save to database
     * 4. Return response
     * 
     * HOW TO TEST:
     * POST /items
     * Body: {
     * "itemName": "Blue Pen",
     * "description": "Smooth writing",
     * "stockQuantity": 100,
     * "categoryId": 1
     * }
     * 
     * Check database:
     * SELECT * FROM items → New row added ✓
     */
    public ItemResponseDTO createItem(ItemDTO itemDTO) {
        // Business Rule 1: Category must exist
        Category category = categoryRepository.findById(itemDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with ID: " + itemDTO.getCategoryId()));

        // Optional: Check if item name already exists
        if (itemRepository.existsByItemName(itemDTO.getItemName())) {
            throw new IllegalArgumentException("Item already exists: " + itemDTO.getItemName());
        }

        // Create item entity
        Item item = new Item();
        item.setItemName(itemDTO.getItemName());
        item.setDescription(itemDTO.getDescription());
        item.setStockQuantity(itemDTO.getStockQuantity());
        item.setCategory(category);

        // Save to database
        Item savedItem = itemRepository.save(item);

        // Return response DTO
        return dtoMapper.toItemResponseDTO(savedItem);
    }

    /**
     * GET all items
     * 
     * USAGE: GET /items
     * Returns all items with category names
     * 
     * FRONTEND displays:
     * - Blue Pen | Stock: 150 | Category: Writing Supplies | [Edit] [Delete]
     * - Notebook | Stock: 45 | Category: Paper Products | [Edit] [Delete]
     */
    public List<ItemResponseDTO> getAllItems() {
        return itemRepository.findAll().stream()
                .map(dtoMapper::toItemResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET item by ID
     * 
     * USAGE: GET /items/1
     * Returns single item details
     */
    public ItemResponseDTO getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + id));

        return dtoMapper.toItemResponseDTO(item);
    }

    /**
     * GET items by category
     * 
     * USAGE: GET /items/category/1
     * Returns all items in a specific category
     * 
     * WHY USEFUL: Users want to browse by category
     * "Show me all Writing Supplies"
     */
    public List<ItemResponseDTO> getItemsByCategory(Long categoryId) {
        // Verify category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with ID: " + categoryId);
        }

        return itemRepository.findByCategoryCategoryId(categoryId).stream()
                .map(dtoMapper::toItemResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * SEARCH items by name
     * 
     * USAGE: GET /items/search?name=pen
     * Finds: "Blue Pen", "Pen Set", "Pencil"
     * 
     * WHY NEEDED: Users need search functionality
     */
    public List<ItemResponseDTO> searchItems(String searchTerm) {
        return itemRepository.findByItemNameContainingIgnoreCase(searchTerm).stream()
                .map(dtoMapper::toItemResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET items with low stock
     * 
     * USAGE: GET /items/low-stock?threshold=10
     * Returns items with stock < 10
     * 
     * WHY NEEDED: Alert admins to reorder items
     * "These items are running low - order more!"
     */
    public List<ItemResponseDTO> getLowStockItems(Integer threshold) {
        return itemRepository.findByStockQuantityLessThan(threshold).stream()
                .map(dtoMapper::toItemResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * UPDATE item
     * 
     * BUSINESS RULES:
     * 1. Item must exist
     * 2. New category must exist
     * 3. Stock must be >= 0
     * 
     * USAGE: PUT /items/1
     * Body: {
     * "itemName": "Updated Name",
     * "description": "New description",
     * "stockQuantity": 200,
     * "categoryId": 1
     * }
     */
    public ItemResponseDTO updateItem(Long id, ItemDTO itemDTO) {
        // Check if item exists
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + id));

        // Verify new category exists
        Category category = categoryRepository.findById(itemDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with ID: " + itemDTO.getCategoryId()));

        // Check if new name conflicts with another item
        if (!item.getItemName().equals(itemDTO.getItemName()) &&
                itemRepository.existsByItemName(itemDTO.getItemName())) {
            throw new IllegalArgumentException("Item already exists: " + itemDTO.getItemName());
        }

        // Update fields
        item.setItemName(itemDTO.getItemName());
        item.setDescription(itemDTO.getDescription());
        item.setStockQuantity(itemDTO.getStockQuantity());
        item.setCategory(category);

        // Save and return
        Item updatedItem = itemRepository.save(item);
        return dtoMapper.toItemResponseDTO(updatedItem);
    }

    /**
     * UPDATE only stock quantity
     * 
     * BUSINESS SCENARIO:
     * - Manager receives stock delivery
     * - Only needs to update quantity, not other fields
     * - Simpler than full update
     * 
     * USAGE: PUT /items/1/stock
     * Body: {"stockQuantity": 250}
     * 
     * HOW TO VERIFY IT'S WORKING:
     * 1. Current stock: 100
     * 2. Send PUT request with stockQuantity: 250
     * 3. GET /items/1 → stockQuantity should be 250 ✓
     */
    public ItemResponseDTO updateStock(Long id, UpdateStockDTO stockDTO) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + id));

        // Update stock
        item.setStockQuantity(stockDTO.getStockQuantity());

        Item updatedItem = itemRepository.save(item);
        return dtoMapper.toItemResponseDTO(updatedItem);
    }

    /**
     * REDUCE stock (called when request is approved)
     * 
     * BUSINESS SCENARIO:
     * 1. User requests 5 pens
     * 2. Manager approves request
     * 3. This method reduces stock by 5
     * 
     * BUSINESS RULES:
     * - Can't reduce below 0
     * - If not enough stock, throw error
     * 
     * NOTE: Member 2 will call this method from RequestService
     */
    public void reduceStock(Long id, Integer quantity) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + id));

        // Business Rule: Check sufficient stock
        if (item.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " +
                    item.getStockQuantity() + ", Requested: " + quantity);
        }

        // Reduce stock
        item.setStockQuantity(item.getStockQuantity() - quantity);
        itemRepository.save(item);
    }

    /**
     * DELETE item
     * 
     * USAGE: DELETE /items/1
     * 
     * BUSINESS RULE: Only ADMIN should be able to delete
     * (Enforced later in security configuration)
     */
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + id));

        itemRepository.delete(item);
    }

    /**
     * GET total stock count (all items combined)
     * 
     * USAGE: For dashboard/reports
     * "Total items in inventory: 1,234"
     */
    public Long getTotalStockCount() {
        Long total = itemRepository.getTotalStockCount();
        return total != null ? total : 0L;
    }
}

