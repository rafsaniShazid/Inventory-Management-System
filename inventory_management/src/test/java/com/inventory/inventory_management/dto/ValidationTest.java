package com.inventory.inventory_management.dto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * TEST: Validation Annotations on DTOs
 * 
 * PURPOSE: Verify all DTO validation rules work correctly without Spring
 * 
 * TESTS COVER:
 * 1. RequestDTO validation (required fields, email format, quantity range)
 * 2. ReviewRequestDTO validation (status required, remarks length)
 * 3. ItemDTO validation (name length, stock non-negative)
 * 4. CategoryDTO validation (name required and length)
 */
class ValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * TEST: RequestDTO - all valid data passes
     */
    @Test
    void testRequestDTO_Valid() {
        RequestDTO requestDTO = new RequestDTO(
            1L,
            5,
            "John Doe",
            "john@example.com"
        );

        Set<ConstraintViolation<RequestDTO>> violations = validator.validate(requestDTO);
        assertTrue(violations.isEmpty(), "Valid RequestDTO should have no violations");
    }

    /**
     * TEST: RequestDTO - null itemId fails
     */
    @Test
    void testRequestDTO_NullItemId() {
        RequestDTO requestDTO = new RequestDTO(
            null, // INVALID
            5,
            "John Doe",
            "john@example.com"
        );

        Set<ConstraintViolation<RequestDTO>> violations = validator.validate(requestDTO);
        assertFalse(violations.isEmpty(), "Null itemId should fail");
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("itemId")));
    }

    /**
     * TEST: RequestDTO - negative quantity fails
     */
    @Test
    void testRequestDTO_NegativeQuantity() {
        RequestDTO requestDTO = new RequestDTO(
            1L,
            -5, // INVALID
            "John Doe",
            "john@example.com"
        );

        Set<ConstraintViolation<RequestDTO>> violations = validator.validate(requestDTO);
        assertFalse(violations.isEmpty(), "Negative quantity should fail");
    }

    /**
     * TEST: RequestDTO - invalid email format fails
     */
    @Test
    void testRequestDTO_InvalidEmailFormat() {
        RequestDTO requestDTO = new RequestDTO(
            1L,
            5,
            "John Doe",
            "not-an-email" // INVALID
        );

        Set<ConstraintViolation<RequestDTO>> violations = validator.validate(requestDTO);
        assertFalse(violations.isEmpty(), "Invalid email format should fail");
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("requesterEmail")));
    }

    /**
     * TEST: ItemDTO - all valid data passes
     */
    @Test
    void testItemDTO_Valid() {
        ItemDTO itemDTO = new ItemDTO(
            "Blue Ballpoint Pen",
            "Smooth writing pen",
            100,
            1L
        );

        Set<ConstraintViolation<ItemDTO>> violations = validator.validate(itemDTO);
        assertTrue(violations.isEmpty(), "Valid ItemDTO should have no violations");
    }

    /**
     * TEST: ItemDTO - negative stock fails
     */
    @Test
    void testItemDTO_NegativeStock() {
        ItemDTO itemDTO = new ItemDTO(
            "Blue Ballpoint Pen",
            "Smooth writing pen",
            -50, // INVALID
            1L
        );

        Set<ConstraintViolation<ItemDTO>> violations = validator.validate(itemDTO);
        assertFalse(violations.isEmpty(), "Negative stock should fail");
    }

    /**
     * TEST: CategoryDTO - valid data passes
     */
    @Test
    void testCategoryDTO_Valid() {
        CategoryDTO categoryDTO = new CategoryDTO(
            "Writing Supplies",
            "Pens, pencils, markers"
        );

        Set<ConstraintViolation<CategoryDTO>> violations = validator.validate(categoryDTO);
        assertTrue(violations.isEmpty(), "Valid CategoryDTO should have no violations");
    }

    /**
     * TEST: CategoryDTO - blank name fails
     */
    @Test
    void testCategoryDTO_BlankName() {
        CategoryDTO categoryDTO = new CategoryDTO(
            "", // INVALID
            "Pens, pencils"
        );

        Set<ConstraintViolation<CategoryDTO>> violations = validator.validate(categoryDTO);
        assertFalse(violations.isEmpty(), "Blank name should fail");
    }

    /**
     * TEST: ReviewRequestDTO - valid data passes
     */
    @Test
    void testReviewRequestDTO_Valid() {
        ReviewRequestDTO reviewDTO = new ReviewRequestDTO(
            com.inventory.inventory_management.entity.RequestStatus.APPROVED,
            "Approved - stock available"
        );

        Set<ConstraintViolation<ReviewRequestDTO>> violations = validator.validate(reviewDTO);
        assertTrue(violations.isEmpty(), "Valid ReviewRequestDTO should have no violations");
    }

    /**
     * TEST: ReviewRequestDTO - null status fails
     */
    @Test
    void testReviewRequestDTO_NullStatus() {
        ReviewRequestDTO reviewDTO = new ReviewRequestDTO(
            null, // INVALID
            "Approved"
        );

        Set<ConstraintViolation<ReviewRequestDTO>> violations = validator.validate(reviewDTO);
        assertFalse(violations.isEmpty(), "Null status should fail");
    }
}
