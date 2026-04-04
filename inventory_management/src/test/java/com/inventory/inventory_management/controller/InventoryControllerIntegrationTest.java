package com.inventory.inventory_management.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class InventoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void categoryAndItemFlowWorksThroughHttpLayer() throws Exception {
        String categoryJson = """
                {
                  "categoryName": "Integration Supplies",
                  "description": "Category created by integration test"
                }
                """;

        String categoryResponse = mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryName").value("Integration Supplies"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode categoryNode = objectMapper.readTree(categoryResponse);
        Long categoryId = categoryNode.get("categoryId").asLong();

        String itemJson = """
                {
                  "itemName": "Integration Blue Pen",
                  "description": "Created through MockMvc",
                  "stockQuantity": 12,
                  "categoryId": %d
                }
                """.formatted(categoryId);

        String itemResponse = mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(itemJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemName").value("Integration Blue Pen"))
                .andExpect(jsonPath("$.categoryId").value(categoryId.intValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode itemNode = objectMapper.readTree(itemResponse);
        Long itemId = itemNode.get("itemId").asLong();

        mockMvc.perform(get("/api/items/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(itemId.intValue()))
                .andExpect(jsonPath("$.itemName").value("Integration Blue Pen"))
                .andExpect(jsonPath("$.categoryName").value("Integration Supplies"));

        mockMvc.perform(get("/api/items/category/{categoryId}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemName").value("Integration Blue Pen"));

        String stockUpdateJson = """
                {
                  "stockQuantity": 20
                }
                """;

        mockMvc.perform(put("/api/items/{id}/stock", itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(stockUpdateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(20));

        mockMvc.perform(delete("/api/items/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/items/{id}", itemId))
                .andExpect(status().isNotFound());
    }

    @Test
    void invalidItemPayloadIsRejectedByValidation() throws Exception {
        String invalidItemJson = """
                {
                  "description": "Missing item name",
                  "stockQuantity": 5,
                  "categoryId": 1
                }
                """;

        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidItemJson))
                .andExpect(status().isBadRequest());
    }
}