package com.example.management.infrastructure.adapters.in.web;

import com.example.management.application.ports.in.*;
import com.example.management.domain.exception.DomainException;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderLine;
import com.example.management.infrastructure.adapters.in.web.dto.CreateOrderRequest;
import com.example.management.infrastructure.adapters.in.web.dto.OrderLineDto;
import com.example.management.infrastructure.adapters.in.web.dto.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controlador REST que expone los endpoints para gestionar pedidos.
 * Adaptador de entrada que convierte peticiones HTTP en llamadas a casos de uso.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderRestController {
    
    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final ConfirmOrderUseCase confirmOrderUseCase;
    private final ShipOrderUseCase shipOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    
    public OrderRestController(
            CreateOrderUseCase createOrderUseCase,
            GetOrderUseCase getOrderUseCase,
            ConfirmOrderUseCase confirmOrderUseCase,
            ShipOrderUseCase shipOrderUseCase,
            CancelOrderUseCase cancelOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.confirmOrderUseCase = confirmOrderUseCase;
        this.shipOrderUseCase = shipOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
    }
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        String orderId = UUID.randomUUID().toString();
        List<OrderLine> orderLines = request.getOrderLines().stream()
                .map(dto -> new OrderLine(dto.getProductId(), dto.getQuantity(), dto.getUnitPrice()))
                .collect(Collectors.toList());
        
        Order order = createOrderUseCase.createOrder(orderId, orderLines);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.fromDomain(order));
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        return getOrderUseCase.getOrder(orderId)
                .map(order -> ResponseEntity.ok(OrderResponse.fromDomain(order)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable String orderId) {
        try {
            Order order = confirmOrderUseCase.confirmOrder(orderId);
            return ResponseEntity.ok(OrderResponse.fromDomain(order));
        } catch (DomainException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PostMapping("/{orderId}/ship")
    public ResponseEntity<OrderResponse> shipOrder(@PathVariable String orderId) {
        try {
            Order order = shipOrderUseCase.shipOrder(orderId);
            return ResponseEntity.ok(OrderResponse.fromDomain(order));
        } catch (DomainException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable String orderId) {
        try {
            Order order = cancelOrderUseCase.cancelOrder(orderId);
            return ResponseEntity.ok(OrderResponse.fromDomain(order));
        } catch (DomainException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<String> handleDomainException(DomainException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
