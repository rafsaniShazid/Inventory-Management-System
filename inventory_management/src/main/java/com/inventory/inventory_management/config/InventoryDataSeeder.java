package com.inventory.inventory_management.config;

import com.inventory.inventory_management.entity.Category;
import com.inventory.inventory_management.entity.Item;
import com.inventory.inventory_management.repository.CategoryRepository;
import com.inventory.inventory_management.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryDataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    @Override
    public void run(String... args) {
        Map<String, Category> categories = seedCategories();
        seedItems(categories);
    }

    private Map<String, Category> seedCategories() {
        Map<String, String> categoryDefinitions = new HashMap<>();
        categoryDefinitions.put("Writing Supplies", "Pens, pencils, markers, and related writing tools.");
        categoryDefinitions.put("Paper Products", "Notebooks, sticky notes, printer paper, and files.");
        categoryDefinitions.put("Office Equipment", "Staplers, scissors, punchers, and desk accessories.");
        categoryDefinitions.put("Art Supplies", "Color pencils, sketch tools, and drawing materials.");

        Map<String, Category> categoryMap = new HashMap<>();

        for (Map.Entry<String, String> entry : categoryDefinitions.entrySet()) {
            String name = entry.getKey();
            String description = entry.getValue();

            Category category = categoryRepository.findByCategoryName(name)
                    .orElseGet(() -> {
                        Category newCategory = new Category();
                        newCategory.setCategoryName(name);
                        newCategory.setDescription(description);
                        Category saved = categoryRepository.save(newCategory);
                        log.info("Seeded category: {}", name);
                        return saved;
                    });

            categoryMap.put(name, category);
        }

        return categoryMap;
    }

    private void seedItems(Map<String, Category> categories) {
        seedItem("Blue Ballpoint Pen", "Smooth writing 0.7mm pen", 150, categories.get("Writing Supplies"));
        seedItem("HB Pencil", "Standard HB wooden pencil", 220, categories.get("Writing Supplies"));
        seedItem("Permanent Marker", "Black permanent marker", 90, categories.get("Writing Supplies"));

        seedItem("A4 Notebook - 200 Pages", "Ruled notebook for daily notes", 75, categories.get("Paper Products"));
        seedItem("Sticky Notes Pack", "Assorted sticky notes set", 130, categories.get("Paper Products"));
        seedItem("A4 Printer Paper (500 Sheets)", "Multipurpose copier paper", 40, categories.get("Paper Products"));

        seedItem("Metal Stapler", "Heavy-duty stapler", 35, categories.get("Office Equipment"));
        seedItem("Paper Punch", "Two-hole office punch", 28, categories.get("Office Equipment"));

        seedItem("Color Pencil Set", "24-color pencil pack", 60, categories.get("Art Supplies"));
        seedItem("Sketch Book A4", "A4 sketching pad", 50, categories.get("Art Supplies"));
    }

    private void seedItem(String itemName, String description, int stockQuantity, Category category) {
        if (category == null) {
            log.warn("Skipping item '{}' because category is missing.", itemName);
            return;
        }

        if (itemRepository.existsByItemName(itemName)) {
            return;
        }

        Item item = new Item();
        item.setItemName(itemName);
        item.setDescription(description);
        item.setStockQuantity(stockQuantity);
        item.setCategory(category);

        itemRepository.save(item);
        log.info("Seeded item: {}", itemName);
    }
}
