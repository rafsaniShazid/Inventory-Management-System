package com.inventory.inventory_management.controller;

import com.inventory.inventory_management.dto.ApiResponseDTO;
import com.inventory.inventory_management.dto.CategoryDTO;
import com.inventory.inventory_management.dto.CategoryResponseDTO;
import com.inventory.inventory_management.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CATEGORY CONTROLLER (Member 1 -YOUR MODULE!)
 * 
 * PURPOSE: Define REST API endpoints for category management
 * This is what Postman/Frontend calls!
 * 
 * BASE URL: /api/categories
 * 
 * ENDPOINTS:
 * - GET /api/categories → Get all categories
 * - GET /api/categories/{id} → Get one category
 * - POST /api/categories → Create category
 * - PUT /api/categories/{id} → Update category
 * - DELETE /api/categories/{id} → Delete category
 * 
 * HOW TO TEST (using Postman):
 * 1. Start application
 * 2. Open Postman
 * 3. Send requests to http://localhost:8080/api/categories
 * 
 * @RestController → Makes this a REST API controller
 * @RequestMapping → Base path for all endpoints
 * @RequiredArgsConstructor → Lombok injects dependencies
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * CREATE new category
     * 
     * ENDPOINT: POST /api/categories
     * 
     * REQUEST:
     * POST http://localhost:8080/api/categories
     * Headers: Content-Type: application/json
     * Body: {
     * "categoryName": "Writing Supplies",
     * "description": "Pens, pencils, markers"
     * }
     * 
     * RESPONSE (201 Created):
     * {
     * "categoryId": 1,
     * "categoryName": "Writing Supplies",
     * "description": "Pens, pencils, markers",
     * "itemCount": 0
     * }
     * 
     * ERROR CASES:
     * - Empty name → 400 Bad Request: "Category name is required"
     * - Duplicate name → 400 Bad Request: "Category already exists"
     * 
     * @Valid → Triggers DTO validation
     * @RequestBody → Read JSON from request body
     *              ResponseEntity → Control HTTP status code
     */
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @Valid @RequestBody CategoryDTO categoryDTO) {

        CategoryResponseDTO response = categoryService.createCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET all categories
     * 
     * ENDPOINT: GET /api/categories
     * 
     * REQUEST:
     * GET http://localhost:8080/api/categories
     * 
     * RESPONSE (200 OK):
     * [
     * {
     * "categoryId": 1,
     * "categoryName": "Writing Supplies",
     * "description": "Pens, pencils",
     * "itemCount": 5
     * },
     * {
     * "categoryId": 2,
     * "categoryName": "Paper Products",
     * "description": "Notebooks, paper",
     * "itemCount": 3
     * }
     * ]
     * 
     * HOW TO VERIFY IT'S WORKING:
     * 1. Create 2-3 categories using POST
     * 2. Call GET /api/categories
     * 3. Should see all categories in response ✓
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * GET single category by ID
     * 
     * ENDPOINT: GET /api/categories/{id}
     * 
     * REQUEST:
     * GET http://localhost:8080/api/categories/1
     * 
     * RESPONSE (200 OK):
     * {
     * "categoryId": 1,
     * "categoryName": "Writing Supplies",
     * "description": "Pens, pencils",
     * "itemCount": 5
     * }
     * 
     * ERROR CASE:
     * GET /api/categories/999 (doesn't exist)
     * → 404 Not Found: "Category not found with ID: 999"
     * 
     * @PathVariable → Extract {id} from URL
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        CategoryResponseDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * UPDATE category
     * 
     * ENDPOINT: PUT /api/categories/{id}
     * 
     * REQUEST:
     * PUT http://localhost:8080/api/categories/1
     * Body: {
     * "categoryName": "Updated Name",
     * "description": "Updated description"
     * }
     * 
     * RESPONSE (200 OK):
     * {
     * "categoryId": 1,
     * "categoryName": "Updated Name",
     * "description": "Updated description",
     * "itemCount": 5
     * }
     * 
     * ERROR CASES:
     * - ID doesn't exist → 404 Not Found
     * - New name conflicts → 400 Bad Request
     * 
     * HOW TO VERIFY:
     * 1. Create category: "Writing Supplies"
     * 2. Update to "Office Supplies"
     * 3. GET the category → Should show new name ✓
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO) {

        CategoryResponseDTO updated = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE category
     * 
     * ENDPOINT: DELETE /api/categories/{id}
     * 
     * REQUEST:
     * DELETE http://localhost:8080/api/categories/1
     * 
     * RESPONSE (200 OK):
     * {
     * "success": true,
     * "message": "Category deleted successfully"
     * }
     * 
     * ERROR CASES:
     * - ID doesn't exist → 404 Not Found
     * - Category has items → 400 Bad Request: "Cannot delete category with items"
     * 
     * BUSINESS RULE:
     * Can't delete category if it has items (enforced in service)
     * Must delete items first, then delete category
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Category deleted successfully"));
    }
}
