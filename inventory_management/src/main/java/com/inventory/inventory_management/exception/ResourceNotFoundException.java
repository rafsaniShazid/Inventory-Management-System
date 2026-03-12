package com.inventory.inventory_management.exception;

/**
 * Custom exception for resource not found errors
 * 
 * USAGE: When item/category doesn't exist
 * 
 * Example:
 * Item item = itemRepository.findById(999)
 * .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID:
 * 999"));
 * 
 * WHY CUSTOM exception (instead of generic Exception):
 * - More specific error handling
 * - Returns proper HTTP status (404 Not Found)
 * - Clean error messages to users
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
