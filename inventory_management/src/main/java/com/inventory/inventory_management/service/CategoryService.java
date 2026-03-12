package com.inventory.inventory_management.service;

import com.inventory.inventory_management.dto.CategoryDTO;
import com.inventory.inventory_management.dto.CategoryResponseDTO;
import com.inventory.inventory_management.entity.Category;
import com.inventory.inventory_management.exception.ResourceNotFoundException;
import com.inventory.inventory_management.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CATEGORY SERVICE (Member 1 - YOUR MODULE!)
 * 
 * PURPOSE: Business logic for category management
 * This is the "BRAIN" of your category module!
 * 
 * RESPONSIBILITIES:
 * - Validate data before saving
 * - Check business rules
 * - Convert between Entity and DTO
 * - Handle errors gracefully
 * 
 * WHY SERVICE LAYER:
 * Controller → receives request, calls service
 * Service → business logic, validation
 * Repository → database operations
 * 
 * This separation makes code clean and testable!
 */
@Service
@RequiredArgsConstructor // Lombok creates constructor with required fields
@Transactional // Ensures database operations are atomic
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * CREATE new category
     * 
     * BUSINESS RULES:
     * 1. Category name must be unique
     * 2. Validate input (done by DTO @Valid)
     * 
     * FLOW:
     * 1. Check if category name already exists
     * 2. If exists → throw error
     * 3. If not → create and save
     * 4. Return response DTO
     * 
     * HOW TO TEST:
     * 1. POST /categories with {"categoryName": "Pens"}
     * 2. Check database → new row in categories table ✓
     * 3. Try again with same name → Error ✓
     */
    public CategoryResponseDTO createCategory(CategoryDTO categoryDTO) {
        // Business Rule: Check if category already exists
        if (categoryRepository.existsByCategoryName(categoryDTO.getCategoryName())) {
            throw new IllegalArgumentException("Category already exists: " + categoryDTO.getCategoryName());
        }

        // Convert DTO to Entity
        Category category = new Category();
        category.setCategoryName(categoryDTO.getCategoryName());
        category.setDescription(categoryDTO.getDescription());

        // Save to database
        Category savedCategory = categoryRepository.save(category);

        // Convert Entity to ResponseDTO and return
        return convertToResponseDTO(savedCategory);
    }

    /**
     * GET all categories
     * 
     * RETURNS: List of all categories with item counts
     * 
     * USAGE: GET /categories
     * Shows dropdown: "Writing Supplies (15 items)", "Paper (8 items)"
     */
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET category by ID
     * 
     * THROWS: ResourceNotFoundException if not found
     * Returns 404 status to user
     */
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        return convertToResponseDTO(category);
    }

    /**
     * UPDATE category
     * 
     * BUSINESS RULES:
     * 1. Category must exist
     * 2. New name must be unique (if changing name)
     * 
     * USAGE: PUT /categories/1
     * Body: {"categoryName": "Updated Name", "description": "New desc"}
     */
    public CategoryResponseDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        // Check if category exists
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        // Check if new name conflicts with existing category
        if (!category.getCategoryName().equals(categoryDTO.getCategoryName()) &&
                categoryRepository.existsByCategoryName(categoryDTO.getCategoryName())) {
            throw new IllegalArgumentException("Category already exists: " + categoryDTO.getCategoryName());
        }

        // Update fields
        category.setCategoryName(categoryDTO.getCategoryName());
        category.setDescription(categoryDTO.getDescription());

        // Save and return
        Category updatedCategory = categoryRepository.save(category);
        return convertToResponseDTO(updatedCategory);
    }

    /**
     * DELETE category
     * 
     * BUSINESS RULE: Should check if category has items before deleting
     * (Optional: For now, we'll allow deletion - you can enhance this later)
     * 
     * USAGE: DELETE /categories/1
     */
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        // Optional business rule: Don't delete category if it has items
        if (category.getItems() != null && !category.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete category with existing items");
        }

        categoryRepository.delete(category);
    }

    /**
     * HELPER METHOD: Convert Entity to ResponseDTO
     * 
     * WHY: Keep conversion logic in one place
     * Easy to maintain and modify
     */
    private CategoryResponseDTO convertToResponseDTO(Category category) {
        int itemCount = category.getItems() != null ? category.getItems().size() : 0;

        return new CategoryResponseDTO(
                category.getCategoryId(),
                category.getCategoryName(),
                category.getDescription(),
                itemCount);
    }
}
