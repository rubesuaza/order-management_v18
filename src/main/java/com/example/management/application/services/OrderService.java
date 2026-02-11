package com.example.management.application.services;

import com.example.management.application.ports.in.*;
import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderLine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación que implementa los casos de uso de gestión de pedidos.
 */
@Service
@Transactional
public class OrderService implements CreateOrderUseCase, GetOrderUseCase, 
        ConfirmOrderUseCase, ShipOrderUseCase, CancelOrderUseCase {
    
    private final OrderRepository orderRepository;
    
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    @Override
    public Order createOrder(String orderId, List<OrderLine> orderLines) {
        if (orderRepository.existsById(orderId)) {
            throw new IllegalArgumentException("Ya existe un pedido con el identificador: " + orderId);
        }
        
        Order order = Order.create(orderId, orderLines);
        return orderRepository.save(order);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrder(String orderId) {
        return orderRepository.findById(orderId);
    }
    
    @Override
    public Order confirmOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + orderId));
        
        order.confirm();
        return orderRepository.save(order);
    }
    
    @Override
    public Order shipOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + orderId));
        
        order.ship();
        return orderRepository.save(order);
    }
    
    @Override
    public Order cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + orderId));
        
        order.cancel();
        return orderRepository.save(order);
    }
}
