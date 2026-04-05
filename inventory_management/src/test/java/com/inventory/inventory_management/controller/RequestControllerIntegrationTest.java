package com.inventory.inventory_management.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.inventory_management.entity.Category;
import com.inventory.inventory_management.entity.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.inventory.inventory_management.entity.RequestItem;

import com.inventory.inventory_management.repository.CategoryRepository;
import com.inventory.inventory_management.repository.ItemRepository;
import com.inventory.inventory_management.repository.RequestRepository;
import com.inventory.inventory_management.entity.Request;
import com.inventory.inventory_management.entity.RequestStatus;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RequestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Test
    @WithMockUser(roles = "USER")
    void requestLifecycleWorksThroughHttpLayer() throws Exception {
        Long itemId = createItem("Request Flow Pen", "Pen used for request integration test", 10);

        String requestJson = """
                {
                  "items": [
                    { "itemId": %d, "quantity": 3 }
                  ],
                  "requesterName": "Jane Doe",
                  "requesterEmail": "jane@example.com"
                }
                """.formatted(itemId);

        String requestResponse = mockMvc.perform(post("/api/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items[0].itemId").value(itemId.intValue()))
                .andExpect(jsonPath("$.items[0].quantity").value(3))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode requestNode = objectMapper.readTree(requestResponse);
        Long requestId = requestNode.get("requestId").asLong();

        mockMvc.perform(get("/api/requests/{id}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(requestId.intValue()))
                .andExpect(jsonPath("$.requesterEmail").value("jane@example.com"));

        mockMvc.perform(get("/api/requests/status/{status}", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestId").value(requestId.intValue()));

        mockMvc.perform(get("/api/requests/email/{email}", "jane@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestId").value(requestId.intValue()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanReviewAndDeleteRequests() throws Exception {
        // Create request as USER (via repository, not API)
        Category category = categoryRepository.findByCategoryName("Admin Test Category")
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setCategoryName("Admin Test Category");
                    newCategory.setDescription("Integration test category");
                    return categoryRepository.save(newCategory);
                });

        Item item = new Item();
        item.setItemName("Admin Test Pen");
        item.setDescription("Pen for admin test");
        item.setStockQuantity(10);
        item.setCategory(category);
        Long itemId = itemRepository.save(item).getItemId();

        Long requestId = createRequestViaRepository(itemId, 3, "Test User", "test@example.com");

        // Admin can review request
        String reviewJson = """
                {
                  "status": "APPROVED",
                  "reviewRemarks": "Approved in integration test"
                }
                """;

        mockMvc.perform(put("/api/requests/{id}/review", requestId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(reviewJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.reviewRemarks").value("Approved in integration test"));

        // Verify stock was decreased
        mockMvc.perform(get("/api/items/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(7));

        // Admin can delete pending requests
        Long pendingRequestId = createRequestViaRepository(itemId, 1, "Another User", "another@example.com");

        mockMvc.perform(delete("/api/requests/{id}", pendingRequestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/requests/{id}", pendingRequestId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void invalidRequestPayloadIsRejectedByValidation() throws Exception {
        String invalidRequestJson = """
                {
                  "items": [],
                  "requesterName": "",
                  "requesterEmail": "not-an-email"
                }
                """;

        mockMvc.perform(post("/api/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }

    private Long createItem(String itemName, String description, int stockQuantity) throws Exception {
        Category category = categoryRepository.findByCategoryName("Request Integration Category")
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setCategoryName("Request Integration Category");
                    newCategory.setDescription("Integration test category");
                    return categoryRepository.save(newCategory);
                });

        Item item = new Item();
        item.setItemName(itemName);
        item.setDescription(description);
        item.setStockQuantity(stockQuantity);
        item.setCategory(category);

        return itemRepository.save(item).getItemId();
    }

    private Long createRequest(Long itemId, int requestedQuantity, String requesterName, String requesterEmail)
            throws Exception {
        String requestJson = """
                {
                  "items": [
                    { "itemId": %d, "quantity": %d }
                  ],
                  "requesterName": "%s",
                  "requesterEmail": "%s"
                }
                """.formatted(itemId, requestedQuantity, requesterName, requesterEmail);

        String requestResponse = mockMvc.perform(post("/api/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(requestResponse).get("requestId").asLong();
    }

    private Long createRequestViaRepository(Long itemId, int requestedQuantity, String requesterName, String requesterEmail) {
        Item item = itemRepository.findById(itemId).orElseThrow();

        Request request = new Request();
        request.setRequesterName(requesterName);
        request.setRequesterEmail(requesterEmail);
        request.setStatus(RequestStatus.PENDING);

        RequestItem requestItem = new RequestItem();
        requestItem.setRequest(request);
        requestItem.setItem(item);
        requestItem.setQuantity(requestedQuantity);

        request.getItems().add(requestItem);

        return requestRepository.save(request).getRequestId();
    }

}
