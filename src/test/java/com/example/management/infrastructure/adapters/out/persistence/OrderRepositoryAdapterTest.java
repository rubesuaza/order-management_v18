package com.example.management.infrastructure.adapters.out.persistence;

import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderLine;
import com.example.management.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(OrderRepositoryAdapter.class)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
class OrderRepositoryAdapterTest {
    
    @Autowired
    private OrderJpaRepository jpaRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
    }
    
    @Test
    @DisplayName("Debe guardar un pedido correctamente")
    void shouldSaveOrder() {
        OrderLine line1 = new OrderLine("PRODUCT-1", 2, new BigDecimal("10.00"));
        OrderLine line2 = new OrderLine("PRODUCT-2", 1, new BigDecimal("5.50"));
        Order order = Order.create("ORDER-1", List.of(line1, line2));
        
        Order savedOrder = orderRepository.save(order);
        
        assertNotNull(savedOrder);
        assertEquals("ORDER-1", savedOrder.getId());
        assertEquals(OrderStatus.CREATED, savedOrder.getStatus());
        assertEquals(new BigDecimal("25.50"), savedOrder.getTotal());
        assertEquals(2, savedOrder.getOrderLines().size());
    }
    
    @Test
    @DisplayName("Debe encontrar un pedido por su identificador")
    void shouldFindOrderById() {
        OrderLine line = new OrderLine("PRODUCT-1", 2, new BigDecimal("10.00"));
        Order order = Order.create("ORDER-1", List.of(line));
        orderRepository.save(order);
        
        Optional<Order> foundOrder = orderRepository.findById("ORDER-1");
        
        assertTrue(foundOrder.isPresent());
        assertEquals("ORDER-1", foundOrder.get().getId());
        assertEquals(OrderStatus.CREATED, foundOrder.get().getStatus());
    }
    
    @Test
    @DisplayName("Debe retornar Optional vacío cuando el pedido no existe")
    void shouldReturnEmptyWhenOrderNotFound() {
        Optional<Order> foundOrder = orderRepository.findById("NON-EXISTENT");
        
        assertFalse(foundOrder.isPresent());
    }
    
    @Test
    @DisplayName("Debe verificar si un pedido existe")
    void shouldCheckIfOrderExists() {
        OrderLine line = new OrderLine("PRODUCT-1", 1, new BigDecimal("10.00"));
        Order order = Order.create("ORDER-1", List.of(line));
        orderRepository.save(order);
        
        assertTrue(orderRepository.existsById("ORDER-1"));
        assertFalse(orderRepository.existsById("NON-EXISTENT"));
    }
    
    @Test
    @DisplayName("Debe persistir y restaurar el estado del pedido correctamente")
    void shouldPersistAndRestoreOrderStatus() {
        OrderLine line = new OrderLine("PRODUCT-1", 1, new BigDecimal("10.00"));
        Order order = Order.create("ORDER-1", List.of(line));
        order.confirm();
        orderRepository.save(order);
        
        Optional<Order> foundOrder = orderRepository.findById("ORDER-1");
        
        assertTrue(foundOrder.isPresent());
        assertEquals(OrderStatus.CONFIRMED, foundOrder.get().getStatus());
    }
    
    @Test
    @DisplayName("Debe persistir y restaurar un pedido enviado correctamente")
    void shouldPersistAndRestoreShippedOrder() {
        OrderLine line = new OrderLine("PRODUCT-1", 1, new BigDecimal("10.00"));
        Order order = Order.create("ORDER-1", List.of(line));
        order.confirm();
        order.ship();
        orderRepository.save(order);
        
        Optional<Order> foundOrder = orderRepository.findById("ORDER-1");
        
        assertTrue(foundOrder.isPresent());
        assertEquals(OrderStatus.SHIPPED, foundOrder.get().getStatus());
    }
    
    @Test
    @DisplayName("Debe persistir y restaurar un pedido cancelado correctamente")
    void shouldPersistAndRestoreCancelledOrder() {
        OrderLine line = new OrderLine("PRODUCT-1", 1, new BigDecimal("10.00"));
        Order order = Order.create("ORDER-1", List.of(line));
        order.cancel();
        orderRepository.save(order);
        
        Optional<Order> foundOrder = orderRepository.findById("ORDER-1");
        
        assertTrue(foundOrder.isPresent());
        assertEquals(OrderStatus.CANCELLED, foundOrder.get().getStatus());
    }
    
    @Test
    @DisplayName("Debe actualizar un pedido existente")
    void shouldUpdateExistingOrder() {
        OrderLine line = new OrderLine("PRODUCT-1", 1, new BigDecimal("10.00"));
        Order order = Order.create("ORDER-1", List.of(line));
        orderRepository.save(order);
        
        order.confirm();
        Order updatedOrder = orderRepository.save(order);
        
        assertEquals(OrderStatus.CONFIRMED, updatedOrder.getStatus());
        Optional<Order> foundOrder = orderRepository.findById("ORDER-1");
        assertTrue(foundOrder.isPresent());
        assertEquals(OrderStatus.CONFIRMED, foundOrder.get().getStatus());
    }
}
