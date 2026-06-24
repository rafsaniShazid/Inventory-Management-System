package com.inventory.inventory_management.dto;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.inventory.inventory_management.entity.Category;
import com.inventory.inventory_management.entity.Item;
import com.inventory.inventory_management.entity.Request;
import com.inventory.inventory_management.entity.RequestItem;

/**
 * CENTRALIZED DTO MAPPER
 *
 * PURPOSE: Convert entities to DTOs in one place
 * Eliminates duplicate mapping code scattered across services
 *
 * WHY THIS MATTERS:
 * - Previously: Each service had its own convertToResponseDTO() method
 * - Problem: Code duplication, harder to maintain
 * - Solution: Single source of truth for all entity-to-DTO conversions
 *
 * BENEFITS:
 * 1. Reusable: All services use the same mapper
 * 2. Testable: Can write dedicated mapper unit tests
 * 3. Maintainable: Change mapping logic in one place
 * 4. Consistent: All conversions follow same pattern
 *
 * USAGE in Services:
 * @RequiredArgsConstructor
 * public class ItemService {
 *     private final DtoMapper dtoMapper;
 *
 *     public ItemResponseDTO getItem(Long id) {
 *         Item item = itemRepository.findById(id)...
 *         return dtoMapper.toItemResponseDTO(item);
 *     }
 * }
 */
@Component
public class DtoMapper {

    /**
     * Convert Category entity to CategoryResponseDTO
     */
    public CategoryResponseDTO toCategoryResponseDTO(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryResponseDTO(
                category.getCategoryId(),
                category.getCategoryName(),
                category.getDescription()
        );
    }

    /**
     * Convert Item entity to ItemResponseDTO
     * Includes category name for user-friendly display
     * Null-safe: handles null category gracefully
     */
    public ItemResponseDTO toItemResponseDTO(Item item) {
        if (item == null) {
            return null;
        }
        
        Long categoryId = null;
        String categoryName = null;
        if (item.getCategory() != null) {
            categoryId = item.getCategory().getCategoryId();
            categoryName = item.getCategory().getCategoryName();
        }
        
        return new ItemResponseDTO(
                item.getItemId(),
                item.getItemName(),
                item.getDescription(),
                item.getStockQuantity(),
                categoryId,
                categoryName
        );
    }

    /**
     * Convert Request entity to RequestResponseDTO
     * Includes the list of requested items for user-friendly display
     * Null-safe: handles missing item associations gracefully
     */
    public RequestResponseDTO toRequestResponseDTO(Request request) {
        if (request == null) {
            return null;
        }

        List<RequestItemDTO> items = request.getItems() == null
                ? Collections.emptyList()
                : request.getItems().stream()
                        .map(this::toRequestItemDTO)
                        .toList();

        return new RequestResponseDTO(
                request.getRequestId(),
                items,
                request.getRequesterName(),
                request.getRequesterEmail(),
                request.getStatus(),
                request.getRequestedAt(),
                request.getReviewedAt(),
                request.getReviewRemarks()
        );
    }

    private RequestItemDTO toRequestItemDTO(RequestItem requestItem) {
        if (requestItem == null) {
            return null;
        }

        Long itemId = null;
        String itemName = null;
        if (requestItem.getItem() != null) {
            itemId = requestItem.getItem().getItemId();
            itemName = requestItem.getItem().getItemName();
        }

        return new RequestItemDTO(
                requestItem.getRequestItemId(),
                itemId,
                itemName,
                requestItem.getQuantity());
    }
}
