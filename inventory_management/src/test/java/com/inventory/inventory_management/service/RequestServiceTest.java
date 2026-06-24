package com.inventory.inventory_management.service;

import com.inventory.inventory_management.dto.DtoMapper;
import com.inventory.inventory_management.dto.RequestDTO;
import com.inventory.inventory_management.dto.RequestItemDTO;
import com.inventory.inventory_management.dto.RequestResponseDTO;
import com.inventory.inventory_management.dto.ReviewRequestDTO;
import com.inventory.inventory_management.entity.Item;
import com.inventory.inventory_management.entity.Request;
import com.inventory.inventory_management.entity.RequestItem;
import com.inventory.inventory_management.entity.RequestStatus;
import com.inventory.inventory_management.exception.ResourceNotFoundException;
import com.inventory.inventory_management.repository.ItemRepository;
import com.inventory.inventory_management.repository.RequestItemRepository;
import com.inventory.inventory_management.repository.RequestRepository;
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
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private DtoMapper dtoMapper;

    @Mock
    private RequestItemRepository requestItemRepository;

    @InjectMocks
    private RequestService requestService;

    private Item item;
    private Request request;
    private RequestItem requestItem;
    private RequestDTO requestDTO;
    private RequestResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setItemId(1L);
        item.setItemName("Pen");
        item.setStockQuantity(20);

        request = new Request();
        request.setRequestId(100L);
        request.setRequesterName("John");
        request.setRequesterEmail("john@example.com");
        request.setStatus(RequestStatus.PENDING);

        requestItem = new RequestItem();
        requestItem.setRequestItemId(200L);
        requestItem.setRequest(request);
        requestItem.setItem(item);
        requestItem.setQuantity(5);
        request.getItems().add(requestItem);

        requestDTO = new RequestDTO(
                List.of(new RequestItemDTO(null, 1L, null, 5)),
                "John",
                "john@example.com");
        responseDTO = new RequestResponseDTO(
                100L,
                List.of(new RequestItemDTO(200L, 1L, "Pen", 5)),
                "John",
                "john@example.com",
                RequestStatus.PENDING,
                null,
                null,
                null);
    }

    @Test
    void submitRequest_Success() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(requestRepository.save(any(Request.class))).thenReturn(request);
        when(dtoMapper.toRequestResponseDTO(request)).thenReturn(responseDTO);

        RequestResponseDTO result = requestService.submitRequest(requestDTO);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(1L, result.getItems().get(0).getItemId());
    }

    @Test
    void submitRequest_ItemNotFound_Throws() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> requestService.submitRequest(requestDTO));
    }

    @Test
    void reviewRequest_Approve_Success() {
        ReviewRequestDTO review = new ReviewRequestDTO(RequestStatus.APPROVED, "ok");
        when(requestRepository.findById(100L)).thenReturn(Optional.of(request));
        when(itemRepository.save(item)).thenReturn(item);
        when(requestRepository.save(request)).thenReturn(request);
        RequestResponseDTO approvedResponse = new RequestResponseDTO(
                100L,
                List.of(new RequestItemDTO(200L, 1L, "Pen", 5)),
                "John",
                "john@example.com",
                RequestStatus.APPROVED,
                null,
                null,
                "ok");
        when(dtoMapper.toRequestResponseDTO(request)).thenReturn(approvedResponse);

        RequestResponseDTO result = requestService.reviewRequest(100L, review);

        assertNotNull(result);
        assertEquals(15, item.getStockQuantity());
        assertEquals(RequestStatus.APPROVED, request.getStatus());
        verify(itemRepository, times(1)).save(item);
        verify(requestRepository, times(1)).save(request);
    }

    @Test
    void reviewRequest_Reject_Success_NoStockChange() {
        ReviewRequestDTO review = new ReviewRequestDTO(RequestStatus.REJECTED, "no");
        when(requestRepository.findById(100L)).thenReturn(Optional.of(request));
        when(requestRepository.save(request)).thenReturn(request);
        RequestResponseDTO rejectedResponse = new RequestResponseDTO(
                100L,
                List.of(new RequestItemDTO(200L, 1L, "Pen", 5)),
                "John",
                "john@example.com",
                RequestStatus.REJECTED,
                null,
                null,
                "no");
        when(dtoMapper.toRequestResponseDTO(request)).thenReturn(rejectedResponse);

        requestService.reviewRequest(100L, review);

        assertEquals(20, item.getStockQuantity());
        assertEquals(RequestStatus.REJECTED, request.getStatus());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void reviewRequest_RequestNotFound_Throws() {
        when(requestRepository.findById(200L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> requestService.reviewRequest(200L, new ReviewRequestDTO(RequestStatus.APPROVED, "ok")));
    }

    @Test
    void reviewRequest_NotPending_Throws() {
        request.setStatus(RequestStatus.REJECTED);
        when(requestRepository.findById(100L)).thenReturn(Optional.of(request));

        assertThrows(IllegalStateException.class,
                () -> requestService.reviewRequest(100L, new ReviewRequestDTO(RequestStatus.APPROVED, "ok")));
    }

    @Test
    void reviewRequest_StatusPendingInReviewPayload_Throws() {
        when(requestRepository.findById(100L)).thenReturn(Optional.of(request));

        assertThrows(IllegalArgumentException.class,
                () -> requestService.reviewRequest(100L, new ReviewRequestDTO(RequestStatus.PENDING, "bad")));
    }

    @Test
    void reviewRequest_InsufficientStock_Throws() {
        requestItem.setQuantity(30);
        when(requestRepository.findById(100L)).thenReturn(Optional.of(request));

        assertThrows(IllegalArgumentException.class,
                () -> requestService.reviewRequest(100L, new ReviewRequestDTO(RequestStatus.APPROVED, "ok")));
    }

    @Test
    void getRequestsByItem_ItemNotFound_Throws() {
        when(itemRepository.existsById(9L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> requestService.getRequestsByItem(9L));
    }

    @Test
    void getRequestsByItem_Success() {
        when(itemRepository.existsById(1L)).thenReturn(true);
        when(requestItemRepository.findByItemItemId(1L)).thenReturn(List.of(requestItem));
        when(dtoMapper.toRequestResponseDTO(request)).thenReturn(responseDTO);

        List<RequestResponseDTO> result = requestService.getRequestsByItem(1L);

        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getRequestId());
    }

    @Test
    void getAllRequests_Success() {
        when(requestRepository.findAll()).thenReturn(List.of(request));
        when(dtoMapper.toRequestResponseDTO(request)).thenReturn(responseDTO);

        List<RequestResponseDTO> result = requestService.getAllRequests();

        assertEquals(1, result.size());
    }

    @Test
    void getRequestById_NotFound_Throws() {
        when(requestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> requestService.getRequestById(99L));
    }

    @Test
    void deleteRequest_Success() {
        when(requestRepository.findById(100L)).thenReturn(Optional.of(request));

        assertDoesNotThrow(() -> requestService.deleteRequest(100L));
        verify(requestRepository).deleteById(100L);
    }

    @Test
    void deleteRequest_NotPending_Throws() {
        request.setStatus(RequestStatus.APPROVED);
        when(requestRepository.findById(100L)).thenReturn(Optional.of(request));

        assertThrows(IllegalStateException.class, () -> requestService.deleteRequest(100L));
    }
}
