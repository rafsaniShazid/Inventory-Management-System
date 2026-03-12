package com.inventory.inventory_management.repository;

import com.inventory.inventory_management.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * CATEGORY REPOSITORY (Member 1 - YOUR MODULE!)
 * 
 * PURPOSE: Handle all database operations for Category
 * 
 * WHAT YOU GET FOR FREE (from JpaRepository):
 * - save(category) → INSERT or UPDATE in database
 * - findById(id) → SELECT * FROM categories WHERE category_id = ?
 * - findAll() → SELECT * FROM categories
 * - deleteById(id) → DELETE FROM categories WHERE category_id = ?
 * - count() → SELECT COUNT(*) FROM categories
 * 
 * Spring Data JPA writes the SQL queries automatically! 🎉
 * 
 * HOW IT WORKS:
 * 1. You call: categoryRepository.findAll()
 * 2. Spring generates: SELECT * FROM categories
 * 3. Returns: List of Category objects
 * 
 * CUSTOM METHODS:
 * You can add custom finder methods and Spring generates queries!
 * Method name pattern: findBy + FieldName
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find category by exact name
     * 
     * Spring automatically generates:
     * SELECT * FROM categories WHERE category_name = ?
     * 
     * WHY USEFUL: Check if category already exists before creating
     * Example: Before adding "Writing Supplies", check it doesn't exist
     */
    Optional<Category> findByCategoryName(String categoryName);

    /**
     * Check if category exists by name
     * 
     * Spring automatically generates:
     * SELECT COUNT(*) > 0 FROM categories WHERE category_name = ?
     * 
     * WHY USEFUL: Validation in service layer
     * if (categoryRepository.existsByCategoryName("Pens")) {
     * throw new Exception("Category already exists!");
     * }
     */
    boolean existsByCategoryName(String categoryName);
}
