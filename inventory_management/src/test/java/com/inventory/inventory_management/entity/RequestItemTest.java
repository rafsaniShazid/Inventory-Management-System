package com.inventory.inventory_management.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * UNIT TESTS FOR REQUEST_ITEM ENTITY
 * 
 * Tests the join table entity that enables many-to-many relationships
 * between requests and items
 */
class RequestItemTest {

    @Test
    void requestItemCanBeCreatedWithValidData() {
        // Arrange
        Request request = new Request();
        request.setRequestId(1L);
        request.setRequesterName("John Doe");
        request.setRequesterEmail("john@example.com");

        Item item = new Item();
        item.setItemId(5L);
        item.setItemName("Blue Pen");
        item.setStockQuantity(100);

        // Act
        RequestItem requestItem = new RequestItem();
        requestItem.setRequest(request);
        requestItem.setItem(item);
        requestItem.setQuantity(50);

        // Assert
        assertNotNull(requestItem);
        assertEquals(1L, requestItem.getRequest().getRequestId());
        assertEquals(5L, requestItem.getItem().getItemId());
        assertEquals(50, requestItem.getQuantity());
        assertEquals("Blue Pen", requestItem.getItem().getItemName());
    }

    @Test
    void quantityCanBeUpdated() {
        // Arrange
        RequestItem requestItem = new RequestItem();
        requestItem.setQuantity(50);

        // Act
        requestItem.setQuantity(75);

        // Assert
        assertEquals(75, requestItem.getQuantity());
    }

    @Test
    void multipleRequestItemsCanBelongToSameRequest() {
        // Arrange
        Request request = new Request();
        request.setRequestId(1L);
        request.setRequesterName("John Doe");
        request.setRequesterEmail("john@example.com");

        Item item1 = new Item();
        item1.setItemId(1L);
        item1.setItemName("Blue Pen");

        Item item2 = new Item();
        item2.setItemId(2L);
        item2.setItemName("Notebook");

        // Act
        RequestItem requestItem1 = new RequestItem();
        requestItem1.setRequest(request);
        requestItem1.setItem(item1);
        requestItem1.setQuantity(50);

        RequestItem requestItem2 = new RequestItem();
        requestItem2.setRequest(request);
        requestItem2.setItem(item2);
        requestItem2.setQuantity(20);

        request.getItems().add(requestItem1);
        request.getItems().add(requestItem2);

        // Assert
        assertEquals(2, request.getItems().size());
        assertTrue(request.getItems().contains(requestItem1));
        assertTrue(request.getItems().contains(requestItem2));
    }

    @Test
    void sameItemCanBelongToMultipleRequests() {
        // Arrange
        Item item = new Item();
        item.setItemId(5L);
        item.setItemName("Blue Pen");
        item.setStockQuantity(200);

        Request request1 = new Request();
        request1.setRequestId(1L);

        Request request2 = new Request();
        request2.setRequestId(2L);

        // Act
        RequestItem requestItem1 = new RequestItem();
        requestItem1.setRequest(request1);
        requestItem1.setItem(item);
        requestItem1.setQuantity(50);

        RequestItem requestItem2 = new RequestItem();
        requestItem2.setRequest(request2);
        requestItem2.setItem(item);
        requestItem2.setQuantity(30);

        item.getRequestItems().add(requestItem1);
        item.getRequestItems().add(requestItem2);

        // Assert
        assertEquals(2, item.getRequestItems().size());
        assertEquals(50, requestItem1.getQuantity());
        assertEquals(30, requestItem2.getQuantity());
        assertEquals("Blue Pen", requestItem1.getItem().getItemName());
        assertEquals("Blue Pen", requestItem2.getItem().getItemName());
    }
}
