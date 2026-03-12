package com.inventory.inventory_management.service;

import com.inventory.inventory_management.dto.CategoryDTO;
import com.inventory.inventory_management.dto.CategoryResponseDTO;
import com.inventory.inventory_management.entity.Category;
import com.inventory.inventory_management.exception.ResourceNotFoundException;
import com.inventory.inventory_management.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;
    private CategoryDTO testCategoryDTO;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setCategoryId(1L);
        testCategory.setCategoryName("Electronics");
        testCategory.setDescription("Electronic items");

        testCategoryDTO = new CategoryDTO();
        testCategoryDTO.setCategoryName("Electronics");
        testCategoryDTO.setDescription("Electronic items");
    }

    @Test
    void testCreateCategory_Success() {
        // Arrange
        when(categoryRepository.existsByCategoryName("Electronics")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Act
        CategoryResponseDTO result = categoryService.createCategory(testCategoryDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Electronics", result.getCategoryName());
        assertEquals("Electronic items", result.getDescription());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testCreateCategory_DuplicateName_ThrowsException() {
        // Arrange
        when(categoryRepository.existsByCategoryName("Electronics")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.createCategory(testCategoryDTO)
        );
        
        assertTrue(exception.getMessage().contains("already exists"));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testGetAllCategories_Success() {
        // Arrange
        Category category2 = new Category();
        category2.setCategoryId(2L);
        category2.setCategoryName("Stationery");
        category2.setDescription("Office supplies");

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(testCategory, category2));

        // Act
        List<CategoryResponseDTO> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Electronics", result.get(0).getCategoryName());
        assertEquals("Stationery", result.get(1).getCategoryName());
    }

    @Test
    void testGetCategoryById_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // Act
        CategoryResponseDTO result = categoryService.getCategoryById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getCategoryId());
        assertEquals("Electronics", result.getCategoryName());
    }

    @Test
    void testGetCategoryById_NotFound_ThrowsException() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> categoryService.getCategoryById(999L)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void testUpdateCategory_Success() {
        // Arrange
        CategoryDTO updateDTO = new CategoryDTO();
        updateDTO.setCategoryName("Updated Electronics");
        updateDTO.setDescription("Updated description");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.existsByCategoryName("Updated Electronics")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Act
        CategoryResponseDTO result = categoryService.updateCategory(1L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testDeleteCategory_Success() {
        // Arrange
        testCategory.setItems(new HashSet<>()); // Empty set of items
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        doNothing().when(categoryRepository).delete(testCategory);

        // Act & Assert
        assertDoesNotThrow(() -> categoryService.deleteCategory(1L));
        verify(categoryRepository, times(1)).delete(testCategory);
    }
}
