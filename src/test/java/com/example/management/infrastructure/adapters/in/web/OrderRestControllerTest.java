package com.example.management.infrastructure.adapters.in.web;

import com.example.management.application.dtos.CreateOrderCommand;
import com.example.management.application.dtos.OrderDto;
import com.example.management.application.dtos.OrderLineDto;
import com.example.management.application.ports.in.*;
import com.example.management.domain.model.OrderId;
import com.example.management.infrastructure.adapters.in.web.dto.CreateOrderRequest;
import com.example.management.infrastructure.adapters.in.web.dto.WebOrderLineDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderRestController.class)
class OrderRestControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private CreateOrderUseCase createOrderUseCase;
    
    @MockBean
    private GetOrderUseCase getOrderUseCase;
    
    @MockBean
    private ConfirmOrderUseCase confirmOrderUseCase;
    
    @MockBean
    private ShipOrderUseCase shipOrderUseCase;
    
    @MockBean
    private CancelOrderUseCase cancelOrderUseCase;
    
    @Test
    @DisplayName("Debe crear un pedido correctamente")
    void shouldCreateOrder() throws Exception {
        OrderDto mockOrderDto = new OrderDto(
                "ORDER-1",
                "CREATED",
                new BigDecimal("25.50"),
                List.of(
                        new OrderLineDto("PRODUCT-1", 2, new BigDecimal("10.00")),
                        new OrderLineDto("PRODUCT-2", 1, new BigDecimal("5.50"))
                )
        );
        
        when(createOrderUseCase.createOrder(any(CreateOrderCommand.class))).thenReturn(mockOrderDto);
        
        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderLines(List.of(
                new WebOrderLineDto("PRODUCT-1", 2, new BigDecimal("10.00")),
                new WebOrderLineDto("PRODUCT-2", 1, new BigDecimal("5.50"))
        ));
        
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("ORDER-1"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.total").value(25.50))
                .andExpect(jsonPath("$.orderLines").isArray())
                .andExpect(jsonPath("$.orderLines.length()").value(2));
    }
    
    @Test
    @DisplayName("Debe obtener un pedido por su identificador")
    void shouldGetOrderById() throws Exception {
        OrderDto mockOrderDto = new OrderDto(
                "ORDER-1",
                "CREATED",
                new BigDecimal("20.00"),
                List.of(new OrderLineDto("PRODUCT-1", 2, new BigDecimal("10.00")))
        );
        
        when(getOrderUseCase.getOrder(any(OrderId.class))).thenReturn(Optional.of(mockOrderDto));
        
        mockMvc.perform(get("/api/orders/ORDER-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ORDER-1"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.total").value(20.00));
    }
    
    @Test
    @DisplayName("Debe retornar 404 cuando el pedido no existe")
    void shouldReturn404WhenOrderNotFound() throws Exception {
        when(getOrderUseCase.getOrder(any(OrderId.class))).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/orders/NON-EXISTENT"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Debe confirmar un pedido correctamente")
    void shouldConfirmOrder() throws Exception {
        OrderDto mockOrderDto = new OrderDto(
                "ORDER-1",
                "CONFIRMED",
                new BigDecimal("10.00"),
                List.of(new OrderLineDto("PRODUCT-1", 1, new BigDecimal("10.00")))
        );
        
        when(confirmOrderUseCase.confirmOrder(any(String.class))).thenReturn(mockOrderDto);
        
        mockMvc.perform(post("/api/orders/ORDER-1/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ORDER-1"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }
    
    @Test
    @DisplayName("Debe enviar un pedido correctamente")
    void shouldShipOrder() throws Exception {
        OrderDto mockOrderDto = new OrderDto(
                "ORDER-1",
                "SHIPPED",
                new BigDecimal("10.00"),
                List.of(new OrderLineDto("PRODUCT-1", 1, new BigDecimal("10.00")))
        );
        
        when(shipOrderUseCase.shipOrder(any(String.class))).thenReturn(mockOrderDto);
        
        mockMvc.perform(post("/api/orders/ORDER-1/ship"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ORDER-1"))
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }
    
    @Test
    @DisplayName("Debe cancelar un pedido correctamente")
    void shouldCancelOrder() throws Exception {
        OrderDto mockOrderDto = new OrderDto(
                "ORDER-1",
                "CANCELLED",
                new BigDecimal("10.00"),
                List.of(new OrderLineDto("PRODUCT-1", 1, new BigDecimal("10.00")))
        );
        
        when(cancelOrderUseCase.cancelOrder(any(String.class))).thenReturn(mockOrderDto);
        
        mockMvc.perform(post("/api/orders/ORDER-1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ORDER-1"))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
    
    @Test
    @DisplayName("Debe retornar 400 cuando se intenta confirmar un pedido ya confirmado")
    void shouldReturn400WhenConfirmingAlreadyConfirmedOrder() throws Exception {
        when(confirmOrderUseCase.confirmOrder(any(String.class)))
                .thenThrow(new IllegalArgumentException("Solo se puede confirmar un pedido en estado CREATED"));
        
        mockMvc.perform(post("/api/orders/ORDER-1/confirm"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Debe validar que la petición de creación tenga al menos una línea")
    void shouldValidateOrderHasAtLeastOneLine() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderLines(List.of());
        
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
