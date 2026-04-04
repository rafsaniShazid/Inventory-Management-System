package com.inventory.inventory_management.repository;

import com.inventory.inventory_management.entity.Category;
import com.inventory.inventory_management.entity.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class InventoryRepositoryIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void itemAndCategoryRepositoriesSupportRealDatabaseQueries() {
        Category writing = new Category();
        writing.setCategoryName("Writing Supplies");
        writing.setDescription("Pens and pencils");
        writing = categoryRepository.save(writing);

        Category paper = new Category();
        paper.setCategoryName("Paper Products");
        paper.setDescription("Notebook and sheets");
        paper = categoryRepository.save(paper);

        Item bluePen = new Item();
        bluePen.setItemName("Blue Pen");
        bluePen.setDescription("Smooth writing pen");
        bluePen.setStockQuantity(15);
        bluePen.setCategory(writing);
        itemRepository.save(bluePen);

        Item redPen = new Item();
        redPen.setItemName("Red Pen");
        redPen.setDescription("Red ink pen");
        redPen.setStockQuantity(11);
        redPen.setCategory(writing);
        itemRepository.save(redPen);

        Item notebook = new Item();
        notebook.setItemName("A4 Notebook");
        notebook.setDescription("Ruled notebook");
        notebook.setStockQuantity(5);
        notebook.setCategory(paper);
        itemRepository.save(notebook);

        List<Item> writingItems = itemRepository.findByCategoryCategoryId(writing.getCategoryId());
        List<Item> penSearchResults = itemRepository.findByItemNameContainingIgnoreCase("pen");
        List<Item> lowStockItems = itemRepository.findByStockQuantityLessThan(10);

        assertEquals(2, writingItems.size());
        assertEquals(2, penSearchResults.size());
        assertEquals(1, lowStockItems.size());
        assertEquals("A4 Notebook", lowStockItems.get(0).getItemName());
        assertEquals(31L, itemRepository.getTotalStockCount());

        assertTrue(categoryRepository.existsByCategoryName("Writing Supplies"));
        assertTrue(categoryRepository.findByCategoryName("Paper Products").isPresent());
        assertEquals(2, itemRepository.findByCategoryName("Writing Supplies").size());
    }
}