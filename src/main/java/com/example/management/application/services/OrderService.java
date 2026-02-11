package com.example.management.application.services;

import com.example.management.application.dtos.CreateOrderCommand;
import com.example.management.application.dtos.OrderDto;
import com.example.management.application.dtos.OrderLineDto;
import com.example.management.application.ports.in.*;
import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderLine;
import com.example.management.domain.service.OrderDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación que implementa los casos de uso de gestión de pedidos.
 */
@Service
@Transactional
public class OrderService implements CreateOrderUseCase, GetOrderUseCase, 
        ConfirmOrderUseCase, ShipOrderUseCase, CancelOrderUseCase {
    
    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
    
    public OrderService(OrderRepository orderRepository, OrderDomainService orderDomainService) {
        this.orderRepository = orderRepository;
        this.orderDomainService = orderDomainService;
    }
    
    @Override
    public OrderDto createOrder(CreateOrderCommand command) {
        if (orderRepository.existsById(command.getOrderId())) {
            throw new IllegalArgumentException("Ya existe un pedido con el identificador: " + command.getOrderId());
        }
        
        List<OrderLine> orderLines = command.getOrderLines().stream()
                .map(dto -> new OrderLine(dto.getProductId(), dto.getQuantity(), dto.getUnitPrice()))
                .collect(Collectors.toList());
        
        Order order = Order.create(command.getOrderId(), orderLines);
        Order savedOrder = orderRepository.save(order);
        return toDto(savedOrder);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDto> getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .map(this::toDto);
    }
    
    @Override
    public OrderDto confirmOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + orderId));
        
        orderDomainService.confirmOrder(order);
        Order savedOrder = orderRepository.save(order);
        return toDto(savedOrder);
    }
    
    @Override
    public OrderDto shipOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + orderId));
        
        orderDomainService.shipOrder(order);
        Order savedOrder = orderRepository.save(order);
        return toDto(savedOrder);
    }
    
    @Override
    public OrderDto cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + orderId));
        
        orderDomainService.cancelOrder(order);
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
