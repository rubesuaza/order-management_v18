package com.example.management.infrastructure.adapters.out.persistence;

import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderLine;
import com.example.management.domain.model.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de salida que implementa OrderRepository usando JPA.
 * Convierte entre el modelo de dominio Order y la entidad JPA OrderEntity.
 */
@Component
public class OrderRepositoryAdapter implements OrderRepository {
    
    private final OrderJpaRepository jpaRepository;
    
    public OrderRepositoryAdapter(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Order save(Order order) {
        OrderEntity entity = jpaRepository.findById(order.getId())
                .map(existingEntity -> updateEntity(existingEntity, order))
                .orElseGet(() -> toEntity(order));
        OrderEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }
    
    @Override
    public Optional<Order> findById(String orderId) {
        return jpaRepository.findById(orderId)
                .map(this::toDomain);
    }
    
    @Override
    public boolean existsById(String orderId) {
        return jpaRepository.existsById(orderId);
    }
    
    private OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity(
                order.getId(),
                order.getStatus(),
                order.getTotal()
        );
        
        List<OrderLineEntity> lineEntities = order.getOrderLines().stream()
                .map(line -> {
                    OrderLineEntity lineEntity = new OrderLineEntity(
                            line.getProductId(),
                            line.getQuantity(),
                            line.getUnitPrice()
                    );
                    lineEntity.setOrder(entity);
                    return lineEntity;
                })
                .collect(Collectors.toList());
        
        entity.setOrderLines(lineEntities);
        return entity;
    }
    
    private OrderEntity updateEntity(OrderEntity existingEntity, Order order) {
        existingEntity.setStatus(order.getStatus());
        existingEntity.setTotal(order.getTotal());
        
        // Limpiar líneas existentes y agregar las nuevas
        existingEntity.getOrderLines().clear();
        List<OrderLineEntity> lineEntities = order.getOrderLines().stream()
                .map(line -> {
                    OrderLineEntity lineEntity = new OrderLineEntity(
                            line.getProductId(),
                            line.getQuantity(),
                            line.getUnitPrice()
                    );
                    lineEntity.setOrder(existingEntity);
                    return lineEntity;
                })
                .collect(Collectors.toList());
        
        existingEntity.setOrderLines(lineEntities);
        return existingEntity;
    }
    
    private Order toDomain(OrderEntity entity) {
        List<OrderLine> orderLines = entity.getOrderLines().stream()
                .map(line -> new OrderLine(
                        line.getProductId(),
                        line.getQuantity(),
                        line.getUnitPrice()
                ))
                .collect(Collectors.toList());
        
        Order order = Order.create(entity.getId(), orderLines);
        
        // Restaurar el estado del pedido
        OrderStatus status = entity.getStatus();
        switch (status) {
            case CONFIRMED:
                if (order.getStatus() == OrderStatus.CREATED) {
                    order.confirm();
                }
                break;
            case SHIPPED:
                if (order.getStatus() == OrderStatus.CREATED) {
                    order.confirm();
                }
                if (order.getStatus() == OrderStatus.CONFIRMED) {
                    order.ship();
                }
                break;
            case CANCELLED:
                if (order.getStatus() == OrderStatus.CREATED || order.getStatus() == OrderStatus.CONFIRMED) {
                    order.cancel();
                }
                break;
        }
        
        return order;
    }
}
