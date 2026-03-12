package com.inventory.inventory_management.controller;

import com.inventory.inventory_management.dto.ApiResponseDTO;
import com.inventory.inventory_management.dto.ItemDTO;
import com.inventory.inventory_management.dto.ItemResponseDTO;
import com.inventory.inventory_management.dto.UpdateStockDTO;
import com.inventory.inventory_management.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ITEM CONTROLLER (Member 1 - YOUR MAIN MODULE!)
 * 
 * This is the MAIN API of your inventory system!
 * Users, admins, and Member 2's request module all use these endpoints.
 * 
 * BASE URL: /api/items
 * 
 * FULL ENDPOINT LIST:
 * - GET /api/items → Get all items
 * - GET /api/items/{id} → Get one item
 * - GET /api/items/category/{id} → Get items by category
 * - GET /api/items/search → Search items (query param)
 * - GET /api/items/low-stock → Get low stock items
 * - GET /api/items/stats → Get inventory statistics
 * - POST /api/items → Create new item
 * - PUT /api/items/{id} → Update item
 * - PUT /api/items/{id}/stock → Update only stock
 * - DELETE /api/items/{id} → Delete item
 * 
 * HOW IT RELATES TO PROJECT:
 * - Admins manage items (POST, PUT, DELETE)
 * - Users browse items (GET)
 * - Request module checks stock availability
 * - Dashboard shows statistics
 */
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * CREATE new item
     * 
     * ENDPOINT: POST /api/items
     * 
     * POSTMAN TEST:
     * POST http://localhost:8080/api/items
     * Headers: Content-Type: application/json
     * Body: {
     * "itemName": "Blue Ballpoint Pen",
     * "description": "Smooth writing, 0.7mm tip",
     * "stockQuantity": 150,
     * "categoryId": 1
     * }
     * 
     * RESPONSE (201 Created):
     * {
     * "itemId": 1,
     * "itemName": "Blue Ballpoint Pen",
     * "description": "Smooth writing, 0.7mm tip",
     * "stockQuantity": 150,
     * "categoryId": 1,
     * "categoryName": "Writing Supplies"
     * }
     * 
     * ERROR CASES:
     * - Empty name → 400: "Item name is required"
     * - Negative stock → 400: "Stock cannot be negative"
     * - Invalid categoryId → 404: "Category not found"
     * 
     * ROLE (later): Only ADMIN can create items
     */
    @PostMapping
    public ResponseEntity<ItemResponseDTO> createItem(@Valid @RequestBody ItemDTO itemDTO) {
        ItemResponseDTO response = itemService.createItem(itemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET all items
     * 
     * ENDPOINT: GET /api/items
     * 
     * POSTMAN TEST:
     * GET http://localhost:8080/api/items
     * 
     * RESPONSE (200 OK):
     * [
     * {
     * "itemId": 1,
     * "itemName": "Blue Pen",
     * "description": "Smooth writing",
     * "stockQuantity": 150,
     * "categoryId": 1,
     * "categoryName": "Writing Supplies"
     * },
     * {
     * "itemId": 2,
     * "itemName": "Notebook",
     * "description": "A4 size, 200 pages",
     * "stockQuantity": 45,
     * "categoryId": 2,
     * "categoryName": "Paper Products"
     * }
     * ]
     * 
     * FRONTEND USE:
     * - Display inventory table
     * - Show available items to users
     * - Admin dashboard item list
     */
    @GetMapping
    public ResponseEntity<List<ItemResponseDTO>> getAllItems() {
        List<ItemResponseDTO> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    /**
     * GET single item by ID
     * 
     * ENDPOINT: GET /api/items/{id}
     * 
     * POSTMAN TEST:
     * GET http://localhost:8080/api/items/1
     * 
     * RESPONSE (200 OK):
     * {
     * "itemId": 1,
     * "itemName": "Blue Pen",
     * "stockQuantity": 150,
     * "categoryName": "Writing Supplies"
     * }
     * 
     * ERROR:
     * GET /api/items/999 → 404: "Item not found with ID: 999"
     * 
     * USE CASE: View item details, edit form
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> getItemById(@PathVariable Long id) {
        ItemResponseDTO item = itemService.getItemById(id);
        return ResponseEntity.ok(item);
    }

    /**
     * GET items by category
     * 
     * ENDPOINT: GET /api/items/category/{categoryId}
     * 
     * POSTMAN TEST:
     * GET http://localhost:8080/api/items/category/1
     * 
     * RESPONSE: All items in category 1 (Writing Supplies)
     * 
     * FRONTEND USE:
     * User clicks "Writing Supplies" → Shows only pens, pencils, etc.
     * Filter/browse by category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ItemResponseDTO>> getItemsByCategory(
            @PathVariable Long categoryId) {

        List<ItemResponseDTO> items = itemService.getItemsByCategory(categoryId);
        return ResponseEntity.ok(items);
    }

    /**
     * SEARCH items by name
     * 
     * ENDPOINT: GET /api/items/search?name={searchTerm}
     * 
     * POSTMAN TEST:
     * GET http://localhost:8080/api/items/search?name=pen
     * 
     * RESPONSE: All items containing "pen" (case-insensitive)
     * - "Blue Pen"
     * - "Pen Set"
     * - "Pencil"
     * 
     * FRONTEND USE:
     * Search box: "Find items..."
     * User types "notebook" → Shows all notebooks
     * 
     * @RequestParam → Extract query parameter from URL
     */
    @GetMapping("/search")
    public ResponseEntity<List<ItemResponseDTO>> searchItems(
            @RequestParam(name = "name") String searchTerm) {

        List<ItemResponseDTO> items = itemService.searchItems(searchTerm);
        return ResponseEntity.ok(items);
    }

    /**
     * GET items with low stock
     * 
     * ENDPOINT: GET /api/items/low-stock?threshold={number}
     * 
     * POSTMAN TEST:
     * GET http://localhost:8080/api/items/low-stock?threshold=10
     * 
     * RESPONSE: All items with stock < 10
     * 
     * FRONTEND USE:
     * Admin dashboard: "⚠️ Low Stock Alert"
     * Shows items that need reordering
     * 
     * DEFAULT: If no threshold provided, use 10
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<ItemResponseDTO>> getLowStockItems(
            @RequestParam(name = "threshold", defaultValue = "10") Integer threshold) {

        List<ItemResponseDTO> items = itemService.getLowStockItems(threshold);
        return ResponseEntity.ok(items);
    }

    /**
     * GET inventory statistics
     * 
     * ENDPOINT: GET /api/items/stats
     * 
     * POSTMAN TEST:
     * GET http://localhost:8080/api/items/stats
     * 
     * RESPONSE (200 OK):
     * {
     * "totalItems": 25,
     * "totalStockCount": 1234,
     * "lowStockCount": 3
     * }
     * 
     * FRONTEND USE:
     * Dashboard widgets:
     * - "Total Items: 25"
     * - "Total Stock: 1,234"
     * - "Low Stock Items: 3"
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getInventoryStats() {
        Map<String, Object> stats = new HashMap<>();

        List<ItemResponseDTO> allItems = itemService.getAllItems();
        List<ItemResponseDTO> lowStockItems = itemService.getLowStockItems(10);
        Long totalStock = itemService.getTotalStockCount();

        stats.put("totalItems", allItems.size());
        stats.put("totalStockCount", totalStock);
        stats.put("lowStockCount", lowStockItems.size());

        return ResponseEntity.ok(stats);
    }

    /**
     * UPDATE item (full update)
     * 
     * ENDPOINT: PUT /api/items/{id}
     * 
     * POSTMAN TEST:
     * PUT http://localhost:8080/api/items/1
     * Body: {
     * "itemName": "Updated Name",
     * "description": "Updated description",
     * "stockQuantity": 200,
     * "categoryId": 1
     * }
     * 
     * RESPONSE (200 OK): Updated item details
     * 
     * ERROR CASES:
     * - ID doesn't exist → 404
     * - Invalid category → 404
     * - Negative stock → 400
     * 
     * ROLE (later): ADMIN or MANAGER only
     */
    @PutMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemDTO itemDTO) {

        ItemResponseDTO updated = itemService.updateItem(id, itemDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * UPDATE only stock quantity
     * 
     * ENDPOINT: PUT /api/items/{id}/stock
     * 
     * POSTMAN TEST:
     * PUT http://localhost:8080/api/items/1/stock
     * Body: {
     * "stockQuantity": 300
     * }
     * 
     * RESPONSE (200 OK): Updated item with new stock
     * 
     * WHY SEPARATE ENDPOINT:
     * - Simpler when only updating stock
     * - Manager receives delivery → update stock quickly
     * - Don't need to send all item fields
     * 
     * HOW TO VERIFY IT'S WORKING:
     * 1. GET /api/items/1 → Note current stock (e.g., 100)
     * 2. PUT /api/items/1/stock with {"stockQuantity": 250}
     * 3. GET /api/items/1 again → Stock should be 250 ✓
     * 4. Check database: SELECT * FROM items WHERE item_id = 1
     * → stock_quantity column should show 250 ✓
     */
    @PutMapping("/{id}/stock")
    public ResponseEntity<ItemResponseDTO> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStockDTO stockDTO) {

        ItemResponseDTO updated = itemService.updateStock(id, stockDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE item
     * 
     * ENDPOINT: DELETE /api/items/{id}
     * 
     * POSTMAN TEST:
     * DELETE http://localhost:8080/api/items/1
     * 
     * RESPONSE (200 OK):
     * {
     * "success": true,
     * "message": "Item deleted successfully"
     * }
     * 
     * ERROR:
     * DELETE /api/items/999 → 404: "Item not found"
     * 
     * ROLE (later): Only ADMIN can delete
     * 
     * HOW TO VERIFY:
     * 1. Create item → Note its ID (e.g., 5)
     * 2. DELETE /api/items/5
     * 3. GET /api/items/5 → Should return 404 ✓
     * 4. GET /api/items → Item should not be in list ✓
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Item deleted successfully"));
    }
}
