package com.inventory.inventory_management.dto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.inventory.inventory_management.entity.Category;
import com.inventory.inventory_management.entity.Item;
import com.inventory.inventory_management.entity.Request;
import com.inventory.inventory_management.entity.RequestItem;
import com.inventory.inventory_management.entity.RequestStatus;

/**
 * TEST: DtoMapper
 * 
 * PURPOSE: Verify DTO mapping is correct and handles null relationships safely
 * 
 * TESTS COVER:
 * 1. Category entity → CategoryResponseDTO
 * 2. Item entity → ItemResponseDTO (with null-safety for missing category)
 * 3. Request entity → RequestResponseDTO (with null-safety for missing item)
 * 4. Null inputs return null
 */
class DtoMapperTest {

    private DtoMapper dtoMapper;

    private Category testCategory;
    private Item testItem;
    private Request testRequest;

    @BeforeEach
    void setUp() {
        dtoMapper = new DtoMapper();

        testCategory = new Category();
        testCategory.setCategoryId(1L);
        testCategory.setCategoryName("Writing Supplies");
        testCategory.setDescription("Pens and pencils");

        testItem = new Item();
        testItem.setItemId(1L);
        testItem.setItemName("Blue Pen");
        testItem.setDescription("Smooth writing pen");
        testItem.setStockQuantity(100);
        testItem.setCategory(testCategory);

        testRequest = new Request();
        testRequest.setRequestId(1L);
        testRequest.setRequesterName("John Doe");
        testRequest.setRequesterEmail("john@example.com");
        testRequest.setStatus(RequestStatus.PENDING);
        testRequest.setRequestedAt(LocalDateTime.now());

        // Create RequestItem with the test item
        RequestItem requestItem = new RequestItem();
        requestItem.setRequest(testRequest);
        requestItem.setItem(testItem);
        requestItem.setQuantity(5);
        testRequest.getItems().add(requestItem);
    }

    /**
     * TEST: Category mapping is correct
     */
    @Test
    void testToCategoryResponseDTO() {
        CategoryResponseDTO response = dtoMapper.toCategoryResponseDTO(testCategory);

        assertNotNull(response);
        assertEquals(1L, response.getCategoryId());
        assertEquals("Writing Supplies", response.getCategoryName());
        assertEquals("Pens and pencils", response.getDescription());
    }

    /**
     * TEST: Null category returns null
     */
    @Test
    void testToCategoryResponseDTO_NullInput() {
        CategoryResponseDTO response = dtoMapper.toCategoryResponseDTO(null);
        assertNull(response);
    }

    /**
     * TEST: Item mapping with valid category
     */
    @Test
    void testToItemResponseDTO_WithCategory() {
        ItemResponseDTO response = dtoMapper.toItemResponseDTO(testItem);

        assertNotNull(response);
        assertEquals(1L, response.getItemId());
        assertEquals("Blue Pen", response.getItemName());
        assertEquals(100, response.getStockQuantity());
        assertEquals(1L, response.getCategoryId());
        assertEquals("Writing Supplies", response.getCategoryName());
    }

    /**
     * TEST: Item mapping with NULL category (null-safety check)
     * This verifies the mapper doesn't throw NullPointerException when category is missing
     */
    @Test
    void testToItemResponseDTO_NullCategory_Safe() {
        testItem.setCategory(null);

        ItemResponseDTO response = dtoMapper.toItemResponseDTO(testItem);

        assertNotNull(response);
        assertEquals(1L, response.getItemId());
        assertEquals("Blue Pen", response.getItemName());
        assertNull(response.getCategoryId(), "Category ID should be null when category is missing");
        assertNull(response.getCategoryName(), "Category name should be null when category is missing");
    }

    /**
     * TEST: Null item returns null
     */
    @Test
    void testToItemResponseDTO_NullInput() {
        ItemResponseDTO response = dtoMapper.toItemResponseDTO(null);
        assertNull(response);
    }

    /**
     * TEST: Request mapping with items list
     */
    @Test
    void testToRequestResponseDTO_WithItem() {
        RequestResponseDTO response = dtoMapper.toRequestResponseDTO(testRequest);

        assertNotNull(response);
        assertEquals(1L, response.getRequestId());
        assertEquals(1, response.getItems().size());
        
        RequestItemDTO itemDto = response.getItems().get(0);
        assertEquals(1L, itemDto.getItemId());
        assertEquals("Blue Pen", itemDto.getItemName());
        assertEquals(5, itemDto.getQuantity());
        
        assertEquals("John Doe", response.getRequesterName());
        assertEquals("john@example.com", response.getRequesterEmail());
        assertEquals(RequestStatus.PENDING, response.getStatus());
        assertNotNull(response.getRequestedAt());
    }

    /**
     * TEST: Request mapping with empty items list (null-safety check)
     * This verifies the mapper handles requests with no items
     */
    @Test
    void testToRequestResponseDTO_NullItem_Safe() {
        testRequest.getItems().clear();

        RequestResponseDTO response = dtoMapper.toRequestResponseDTO(testRequest);

        assertNotNull(response);
        assertEquals(1L, response.getRequestId());
        assertEquals(0, response.getItems().size());
        assertEquals("John Doe", response.getRequesterName());
        assertEquals("john@example.com", response.getRequesterEmail());
        assertEquals(RequestStatus.PENDING, response.getStatus());
    }

    /**
     * TEST: Null request returns null
     */
    @Test
    void testToRequestResponseDTO_NullInput() {
        RequestResponseDTO response = dtoMapper.toRequestResponseDTO(null);
        assertNull(response);
    }

    /**
     * TEST: Request mapping preserves all review details with items
     */
    @Test
    void testToRequestResponseDTO_WithReviewDetails() {
        LocalDateTime reviewedAt = LocalDateTime.now();
        testRequest.setStatus(RequestStatus.APPROVED);
        testRequest.setReviewedAt(reviewedAt);
        testRequest.setReviewRemarks("Approved - stock available");

        RequestResponseDTO response = dtoMapper.toRequestResponseDTO(testRequest);

        assertEquals(RequestStatus.APPROVED, response.getStatus());
        assertEquals(reviewedAt, response.getReviewedAt());
        assertEquals("Approved - stock available", response.getReviewRemarks());
        assertEquals(1, response.getItems().size());
        assertEquals(5, response.getItems().get(0).getQuantity());
    }
}
