package com.example.management.application.services;

import com.example.management.application.dtos.CreateOrderCommand;
import com.example.management.application.dtos.OrderDto;
import com.example.management.application.dtos.OrderLineDto;
import com.example.management.application.ports.in.*;
import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderId;
import com.example.management.domain.model.OrderLine;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación que implementa los casos de uso de gestión de pedidos.
 */
public class OrderService implements CreateOrderUseCase, GetOrderUseCase, 
        ConfirmOrderUseCase, ShipOrderUseCase, CancelOrderUseCase {
    
    private final OrderRepository orderRepository;
    
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    @Override
    public OrderDto createOrder(CreateOrderCommand command) {
        OrderId orderId = OrderId.of(UUID.randomUUID().toString());
        
        List<OrderLine> orderLines = command.getOrderLines().stream()
                .map(dto -> new OrderLine(dto.getProductId(), dto.getQuantity(), dto.getUnitPrice()))
                .collect(Collectors.toList());
        
        Order order = Order.create(orderId.getValue(), orderLines);
        Order savedOrder = orderRepository.save(order);
        return toDto(savedOrder);
    }
    
    @Override
    public Optional<OrderDto> getOrder(OrderId orderId) {
        return orderRepository.findById(orderId.getValue())
                .map(this::toDto);
    }
    
    @Override
    public OrderDto confirmOrder(OrderId orderId) {
        Order order = orderRepository.findById(orderId.getValue())
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + orderId.getValue()));
        
        order.confirm();
        Order savedOrder = orderRepository.save(order);
        return toDto(savedOrder);
    }
    
    @Override
    public OrderDto shipOrder(OrderId orderId) {
        Order order = orderRepository.findById(orderId.getValue())
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + orderId.getValue()));
        
        order.ship();
        Order savedOrder = orderRepository.save(order);
        return toDto(savedOrder);
    }
    
    @Override
    public OrderDto cancelOrder(OrderId orderId) {
        Order order = orderRepository.findById(orderId.getValue())
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + orderId.getValue()));
        
        order.cancel();
        Order savedOrder = orderRepository.save(order);
        return toDto(savedOrder);
    }
    
    private OrderDto toDto(Order order) {
        List<OrderLineDto> orderLineDtos = order.getOrderLines().stream()
                .map(line -> new OrderLineDto(
                        line.getProductId(),
                        line.getQuantity(),
                        line.getUnitPrice()
                ))
                .collect(Collectors.toList());
        
        return new OrderDto(
                order.getId(),
                order.getStatus().name(),
                order.getTotal(),
                orderLineDtos
        );
    }
}
