package com.example.management.application.services;

import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.exception.InvalidOrderStateException;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderLine;
import com.example.management.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private OrderLine orderLine;
    private Order order;

    @BeforeEach
    void setUp() {
        orderLine = new OrderLine("PRODUCT-1", 2, new BigDecimal("10.00"));
        order = Order.create("ORDER-1", List.of(orderLine));
    }

    @Test
    @DisplayName("Debe crear un pedido correctamente cuando no existe")
    void shouldCreateOrderWhenNotExists() {
        // Arrange
        when(orderRepository.existsById("ORDER-1")).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order createdOrder = orderService.createOrder("ORDER-1", List.of(orderLine));

        // Assert
        assertNotNull(createdOrder);
        assertEquals("ORDER-1", createdOrder.getId());
        assertEquals(OrderStatus.CREATED, createdOrder.getStatus());
        verify(orderRepository).existsById("ORDER-1");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear un pedido con ID duplicado")
    void shouldThrowExceptionWhenCreatingOrderWithDuplicateId() {
        // Arrange
        when(orderRepository.existsById("ORDER-1")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder("ORDER-1", List.of(orderLine)));

        assertEquals("Ya existe un pedido con el identificador: ORDER-1", exception.getMessage());
        verify(orderRepository).existsById("ORDER-1");
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe obtener un pedido existente por su identificador")
    void shouldGetOrderByIdWhenExists() {
        // Arrange
        when(orderRepository.findById("ORDER-1")).thenReturn(Optional.of(order));

        // Act
        Optional<Order> result = orderService.getOrder("ORDER-1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("ORDER-1", result.get().getId());
        verify(orderRepository).findById("ORDER-1");
    }

    @Test
    @DisplayName("Debe retornar Optional vacío cuando el pedido no existe")
    void shouldReturnEmptyWhenOrderNotFound() {
        // Arrange
        when(orderRepository.findById("NON-EXISTENT")).thenReturn(Optional.empty());

        // Act
        Optional<Order> result = orderService.getOrder("NON-EXISTENT");

        // Assert
        assertFalse(result.isPresent());
        verify(orderRepository).findById("NON-EXISTENT");
    }

    @Test
    @DisplayName("Debe confirmar un pedido existente en estado CREATED")
    void shouldConfirmOrderWhenInCreatedState() {
        // Arrange
        when(orderRepository.findById("ORDER-1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order confirmedOrder = orderService.confirmOrder("ORDER-1");

        // Assert
        assertEquals(OrderStatus.CONFIRMED, confirmedOrder.getStatus());
        verify(orderRepository).findById("ORDER-1");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al confirmar un pedido que no existe")
    void shouldThrowExceptionWhenConfirmingNonExistentOrder() {
        // Arrange
        when(orderRepository.findById("NON-EXISTENT")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.confirmOrder("NON-EXISTENT"));

        assertEquals("Pedido no encontrado: NON-EXISTENT", exception.getMessage());
        verify(orderRepository).findById("NON-EXISTENT");
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe propagar excepción al confirmar un pedido en estado inválido")
    void shouldPropagateExceptionWhenConfirmingOrderInInvalidState() {
        // Arrange
        order.confirm(); // Cambiar a CONFIRMED
        when(orderRepository.findById("ORDER-1")).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(InvalidOrderStateException.class,
                () -> orderService.confirmOrder("ORDER-1"));

        verify(orderRepository).findById("ORDER-1");
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe enviar un pedido existente en estado CONFIRMED")
    void shouldShipOrderWhenInConfirmedState() {
        // Arrange
        order.confirm();
        when(orderRepository.findById("ORDER-1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order shippedOrder = orderService.shipOrder("ORDER-1");

        // Assert
        assertEquals(OrderStatus.SHIPPED, shippedOrder.getStatus());
        verify(orderRepository).findById("ORDER-1");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al enviar un pedido que no existe")
    void shouldThrowExceptionWhenShippingNonExistentOrder() {
        // Arrange
        when(orderRepository.findById("NON-EXISTENT")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.shipOrder("NON-EXISTENT"));

        assertEquals("Pedido no encontrado: NON-EXISTENT", exception.getMessage());
        verify(orderRepository).findById("NON-EXISTENT");
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe propagar excepción al enviar un pedido en estado inválido")
    void shouldPropagateExceptionWhenShippingOrderInInvalidState() {
        // Arrange
        // El pedido está en estado CREATED, no CONFIRMED
        when(orderRepository.findById("ORDER-1")).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(InvalidOrderStateException.class,
                () -> orderService.shipOrder("ORDER-1"));

        verify(orderRepository).findById("ORDER-1");
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe cancelar un pedido existente en estado CREATED")
    void shouldCancelOrderWhenInCreatedState() {
        // Arrange
        when(orderRepository.findById("ORDER-1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order cancelledOrder = orderService.cancelOrder("ORDER-1");

        // Assert
        assertEquals(OrderStatus.CANCELLED, cancelledOrder.getStatus());
        verify(orderRepository).findById("ORDER-1");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe cancelar un pedido existente en estado CONFIRMED")
    void shouldCancelOrderWhenInConfirmedState() {
        // Arrange
        order.confirm();
        when(orderRepository.findById("ORDER-1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order cancelledOrder = orderService.cancelOrder("ORDER-1");

        // Assert
        assertEquals(OrderStatus.CANCELLED, cancelledOrder.getStatus());
        verify(orderRepository).findById("ORDER-1");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al cancelar un pedido que no existe")
    void shouldThrowExceptionWhenCancellingNonExistentOrder() {
        // Arrange
        when(orderRepository.findById("NON-EXISTENT")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.cancelOrder("NON-EXISTENT"));

        assertEquals("Pedido no encontrado: NON-EXISTENT", exception.getMessage());
        verify(orderRepository).findById("NON-EXISTENT");
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe propagar excepción al cancelar un pedido en estado SHIPPED")
    void shouldPropagateExceptionWhenCancellingShippedOrder() {
        // Arrange
        order.confirm();
        order.ship();
        when(orderRepository.findById("ORDER-1")).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(InvalidOrderStateException.class,
                () -> orderService.cancelOrder("ORDER-1"));

        verify(orderRepository).findById("ORDER-1");
        verify(orderRepository, never()).save(any(Order.class));
    }
}
