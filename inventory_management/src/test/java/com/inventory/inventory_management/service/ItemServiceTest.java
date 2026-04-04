package com.inventory.inventory_management.service;

import com.inventory.inventory_management.dto.DtoMapper;
import com.inventory.inventory_management.dto.ItemDTO;
import com.inventory.inventory_management.dto.ItemResponseDTO;
import com.inventory.inventory_management.dto.UpdateStockDTO;
import com.inventory.inventory_management.entity.Category;
import com.inventory.inventory_management.entity.Item;
import com.inventory.inventory_management.exception.ResourceNotFoundException;
import com.inventory.inventory_management.repository.CategoryRepository;
import com.inventory.inventory_management.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private DtoMapper dtoMapper;

    @InjectMocks
    private ItemService itemService;

    private Category category;
    private Item item;
    private ItemDTO itemDTO;
    private ItemResponseDTO itemResponseDTO;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Electronics");

        item = new Item();
        item.setItemId(10L);
        item.setItemName("Laptop");
        item.setDescription("Gaming laptop");
        item.setStockQuantity(10);
        item.setCategory(category);

        itemDTO = new ItemDTO("Laptop", "Gaming laptop", 10, 1L);
        itemResponseDTO = new ItemResponseDTO(10L, "Laptop", "Gaming laptop", 10, 1L, "Electronics");
    }

    @Test
    void createItem_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(itemRepository.existsByItemName("Laptop")).thenReturn(false);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(dtoMapper.toItemResponseDTO(item)).thenReturn(itemResponseDTO);

        ItemResponseDTO result = itemService.createItem(itemDTO);

        assertNotNull(result);
        assertEquals("Laptop", result.getItemName());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItem_CategoryNotFound_Throws() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.createItem(itemDTO));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void createItem_DuplicateName_Throws() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(itemRepository.existsByItemName("Laptop")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> itemService.createItem(itemDTO));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void getAllItems_Success() {
        when(itemRepository.findAll()).thenReturn(List.of(item));
        when(dtoMapper.toItemResponseDTO(item)).thenReturn(itemResponseDTO);

        List<ItemResponseDTO> result = itemService.getAllItems();

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getItemId());
    }

    @Test
    void getItemById_NotFound_Throws() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.getItemById(99L));
    }

    @Test
    void getItemsByCategory_CategoryNotFound_Throws() {
        when(categoryRepository.existsById(2L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> itemService.getItemsByCategory(2L));
    }

    @Test
    void updateItem_Success() {
        ItemDTO update = new ItemDTO("Laptop Pro", "Updated", 15, 1L);
        itemResponseDTO.setItemName("Laptop Pro");

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(itemRepository.existsByItemName("Laptop Pro")).thenReturn(false);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(dtoMapper.toItemResponseDTO(any(Item.class))).thenReturn(itemResponseDTO);

        ItemResponseDTO result = itemService.updateItem(10L, update);

        assertEquals("Laptop Pro", result.getItemName());
        verify(itemRepository).save(item);
    }

    @Test
    void updateItem_DuplicateNewName_Throws() {
        ItemDTO update = new ItemDTO("Phone", "Updated", 15, 1L);

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(itemRepository.existsByItemName("Phone")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> itemService.updateItem(10L, update));
    }

    @Test
    void updateStock_Success() {
        UpdateStockDTO stockDTO = new UpdateStockDTO(50);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        when(dtoMapper.toItemResponseDTO(item)).thenReturn(itemResponseDTO);

        ItemResponseDTO result = itemService.updateStock(10L, stockDTO);

        assertNotNull(result);
        assertEquals(50, item.getStockQuantity());
    }

    @Test
    void reduceStock_Insufficient_Throws() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class, () -> itemService.reduceStock(10L, 50));
    }

    @Test
    void reduceStock_Success() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertDoesNotThrow(() -> itemService.reduceStock(10L, 3));
        assertEquals(7, item.getStockQuantity());
        verify(itemRepository).save(item);
    }

    @Test
    void deleteItem_NotFound_Throws() {
        when(itemRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.deleteItem(100L));
    }

    @Test
    void getTotalStockCount_NullFromRepo_ReturnsZero() {
        when(itemRepository.getTotalStockCount()).thenReturn(null);

        Long result = itemService.getTotalStockCount();

        assertEquals(0L, result);
    }
}
