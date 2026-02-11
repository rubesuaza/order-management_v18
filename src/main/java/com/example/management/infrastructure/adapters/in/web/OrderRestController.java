package com.example.management.infrastructure.adapters.in.web;

import com.example.management.application.dtos.CreateOrderCommand;
import com.example.management.application.dtos.OrderDto;
import com.example.management.application.dtos.OrderLineDto as AppOrderLineDto;
import com.example.management.application.ports.in.*;
import com.example.management.domain.exception.DomainException;
import com.example.management.infrastructure.adapters.in.web.dto.CreateOrderRequest;
import com.example.management.infrastructure.adapters.in.web.dto.OrderLineDto as WebOrderLineDto;
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
        List<AppOrderLineDto> appOrderLines = request.getOrderLines().stream()
                .map(dto -> new AppOrderLineDto(dto.getProductId(), dto.getQuantity(), dto.getUnitPrice()))
                .collect(Collectors.toList());
        
        CreateOrderCommand command = new CreateOrderCommand(orderId, appOrderLines);
        OrderDto orderDto = createOrderUseCase.createOrder(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(orderDto));
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        return getOrderUseCase.getOrder(orderId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable String orderId) {
        OrderDto orderDto = confirmOrderUseCase.confirmOrder(orderId);
        return ResponseEntity.ok(toResponse(orderDto));
    }
    
    @PostMapping("/{orderId}/ship")
    public ResponseEntity<OrderResponse> shipOrder(@PathVariable String orderId) {
        OrderDto orderDto = shipOrderUseCase.shipOrder(orderId);
        return ResponseEntity.ok(toResponse(orderDto));
    }
    
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable String orderId) {
        OrderDto orderDto = cancelOrderUseCase.cancelOrder(orderId);
        return ResponseEntity.ok(toResponse(orderDto));
    }
    
    private OrderResponse toResponse(OrderDto orderDto) {
        List<OrderLineDto> orderLineDtos = orderDto.getOrderLines().stream()
                .map(line -> new OrderLineDto(line.getProductId(), line.getQuantity(), line.getUnitPrice()))
                .collect(Collectors.toList());
        
        return new OrderResponse(
                orderDto.getId(),
                orderDto.getStatus(),
                orderDto.getTotal(),
                orderLineDtos
        );
    }
    
    @ExceptionHandler({DomainException.class, IllegalArgumentException.class})
    public ResponseEntity<String> handleBadRequestException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
