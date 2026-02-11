package com.example.management.infrastructure.adapters.in.web;

import com.example.management.application.dtos.CreateOrderCommand;
import com.example.management.application.dtos.OrderLineDto as AppOrderLineDto;
import com.example.management.application.ports.in.*;
import com.example.management.domain.model.OrderId;
import com.example.management.infrastructure.adapters.in.web.dto.CreateOrderRequest;
import com.example.management.infrastructure.adapters.in.web.dto.OrderLineDto;
import com.example.management.infrastructure.adapters.out.persistence.OrderJpaRepository;
import com.example.management.infrastructure.adapters.out.persistence.OrderRepositoryAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderRestController.class)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
class OrderRestControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CreateOrderUseCase createOrderUseCase;
    
    @Autowired
    private GetOrderUseCase getOrderUseCase;
    
    @Autowired
    private ConfirmOrderUseCase confirmOrderUseCase;
    
    @Autowired
    private ShipOrderUseCase shipOrderUseCase;
    
    @Autowired
    private CancelOrderUseCase cancelOrderUseCase;
    
    @Autowired
    private OrderJpaRepository jpaRepository;
    
    @TestConfiguration
    static class TestConfig {
        @Bean
        public OrderRepositoryAdapter orderRepositoryAdapter(OrderJpaRepository jpaRepository) {
            return new OrderRepositoryAdapter(jpaRepository);
        }
        
        @Bean
        public com.example.management.application.services.OrderService orderService(
                OrderRepositoryAdapter repositoryAdapter) {
            return new com.example.management.application.services.OrderService(repositoryAdapter);
        }
        
        @Bean
        public CreateOrderUseCase createOrderUseCase(com.example.management.application.services.OrderService orderService) {
            return orderService;
        }
        
        @Bean
        public GetOrderUseCase getOrderUseCase(com.example.management.application.services.OrderService orderService) {
            return orderService;
        }
        
        @Bean
        public ConfirmOrderUseCase confirmOrderUseCase(com.example.management.application.services.OrderService orderService) {
            return orderService;
        }
        
        @Bean
        public ShipOrderUseCase shipOrderUseCase(com.example.management.application.services.OrderService orderService) {
            return orderService;
        }
        
        @Bean
        public CancelOrderUseCase cancelOrderUseCase(com.example.management.application.services.OrderService orderService) {
            return orderService;
        }
    }
    
    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
    }
    
    @Test
    @DisplayName("Debe crear un pedido correctamente")
    void shouldCreateOrder() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderLines(List.of(
                new OrderLineDto("PRODUCT-1", 2, new BigDecimal("10.00")),
                new OrderLineDto("PRODUCT-2", 1, new BigDecimal("5.50"))
        ));
        
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.total").value(25.50))
                .andExpect(jsonPath("$.orderLines").isArray())
                .andExpect(jsonPath("$.orderLines.length()").value(2));
    }
    
    @Test
    @DisplayName("Debe obtener un pedido por su identificador")
    void shouldGetOrderById() throws Exception {
        // Crear un pedido primero usando el use case
        CreateOrderRequest createRequest = new CreateOrderRequest();
        createRequest.setOrderLines(List.of(
                new OrderLineDto("PRODUCT-1", 2, new BigDecimal("10.00"))
        ));
        
        String orderId = createOrderUseCase.createOrder(
                new CreateOrderCommand(createRequest.getOrderLines().stream()
                        .map(dto -> new AppOrderLineDto(dto.getProductId(), dto.getQuantity(), dto.getUnitPrice()))
                        .collect(Collectors.toList()))
        ).getId();
        
        mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.total").value(20.00));
    }
    
    @Test
    @DisplayName("Debe retornar 404 cuando el pedido no existe")
    void shouldReturn404WhenOrderNotFound() throws Exception {
        mockMvc.perform(get("/api/orders/NON-EXISTENT"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Debe confirmar un pedido correctamente")
    void shouldConfirmOrder() throws Exception {
        // Crear un pedido usando el use case
        CreateOrderRequest createRequest = new CreateOrderRequest();
        createRequest.setOrderLines(List.of(
                new OrderLineDto("PRODUCT-1", 1, new BigDecimal("10.00"))
        ));
        
        String orderId = createOrderUseCase.createOrder(
                new CreateOrderCommand(createRequest.getOrderLines().stream()
                        .map(dto -> new AppOrderLineDto(dto.getProductId(), dto.getQuantity(), dto.getUnitPrice()))
                        .collect(Collectors.toList()))
        ).getId();
        
        mockMvc.perform(post("/api/orders/" + orderId + "/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }
    
    @Test
    @DisplayName("Debe enviar un pedido correctamente")
    void shouldShipOrder() throws Exception {
        // Crear un pedido usando el use case
        CreateOrderRequest createRequest = new CreateOrderRequest();
        createRequest.setOrderLines(List.of(
                new OrderLineDto("PRODUCT-1", 1, new BigDecimal("10.00"))
        ));
        
        String orderId = createOrderUseCase.createOrder(
                new CreateOrderCommand(createRequest.getOrderLines().stream()
                        .map(dto -> new AppOrderLineDto(dto.getProductId(), dto.getQuantity(), dto.getUnitPrice()))
                        .collect(Collectors.toList()))
        ).getId();
        
        // Confirmar el pedido usando el use case
        confirmOrderUseCase.confirmOrder(OrderId.of(orderId));
        
        mockMvc.perform(post("/api/orders/" + orderId + "/ship"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }
    
    @Test
    @DisplayName("Debe cancelar un pedido correctamente")
    void shouldCancelOrder() throws Exception {
        // Crear un pedido usando el use case
        CreateOrderRequest createRequest = new CreateOrderRequest();
        createRequest.setOrderLines(List.of(
                new OrderLineDto("PRODUCT-1", 1, new BigDecimal("10.00"))
        ));
        
        String orderId = createOrderUseCase.createOrder(
                new CreateOrderCommand(createRequest.getOrderLines().stream()
                        .map(dto -> new AppOrderLineDto(dto.getProductId(), dto.getQuantity(), dto.getUnitPrice()))
                        .collect(Collectors.toList()))
        ).getId();
        
        mockMvc.perform(post("/api/orders/" + orderId + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
    
    @Test
    @DisplayName("Debe retornar 400 cuando se intenta confirmar un pedido ya confirmado")
    void shouldReturn400WhenConfirmingAlreadyConfirmedOrder() throws Exception {
        // Crear un pedido usando el use case
        CreateOrderRequest createRequest = new CreateOrderRequest();
        createRequest.setOrderLines(List.of(
                new OrderLineDto("PRODUCT-1", 1, new BigDecimal("10.00"))
        ));
        
        String orderId = createOrderUseCase.createOrder(
                new CreateOrderCommand(createRequest.getOrderLines().stream()
                        .map(dto -> new AppOrderLineDto(dto.getProductId(), dto.getQuantity(), dto.getUnitPrice()))
                        .collect(Collectors.toList()))
        ).getId();
        
        // Confirmar el pedido usando el use case
        confirmOrderUseCase.confirmOrder(OrderId.of(orderId));
        
        mockMvc.perform(post("/api/orders/" + orderId + "/confirm"))
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
