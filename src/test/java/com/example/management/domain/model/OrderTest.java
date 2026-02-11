package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidOrderStateException;
import com.example.management.domain.exception.OrderValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderTest {

    @Test
    @DisplayName("Un pedido válido se crea en estado CREATED y con total correcto")
    void validOrderIsCreatedWithCorrectTotal() {
        OrderLine line1 = new OrderLine("PRODUCT-1", 2, new BigDecimal("10.00"));
        OrderLine line2 = new OrderLine("PRODUCT-2", 1, new BigDecimal("5.50"));

        Order order = Order.create("ORDER-1", List.of(line1, line2));

        assertEquals("ORDER-1", order.getId());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(new BigDecimal("25.50"), order.getTotal());
    }

    @Test
    @DisplayName("Un pedido debe tener al menos una línea")
    void orderMustHaveAtLeastOneLine() {
        assertThrows(OrderValidationException.class,
                () -> Order.create("ORDER-1", List.of()));
    }

    @Test
    @DisplayName("El identificador de pedido no puede ser nulo ni vacío")
    void orderIdMustNotBeBlank() {
        OrderLine line = new OrderLine("PRODUCT-1", 1, new BigDecimal("10.00"));

        assertThrows(OrderValidationException.class,
                () -> Order.create(null, List.of(line)));

        assertThrows(OrderValidationException.class,
                () -> Order.create("   ", List.of(line)));
    }

    @Test
    @DisplayName("No se permiten líneas nulas en el pedido")
    void orderMustNotContainNullLines() {
        OrderLine line = new OrderLine("PRODUCT-1", 1, new BigDecimal("10.00"));

        assertThrows(OrderValidationException.class,
                () -> Order.create("ORDER-1", Arrays.asList(line, null)));
    }

    @Test
    @DisplayName("Solo se puede confirmar un pedido en estado CREATED")
    void confirmOnlyFromCreated() {
        OrderLine line = new OrderLine("PRODUCT-1", 1, new BigDecimal("10.00"));
        Order order = Order.create("ORDER-1", List.of(line));

        order.confirm();
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());

        assertThrows(InvalidOrderStateException.class, order::confirm);
    }

    @Test
    @DisplayName("Solo se puede enviar un pedido en estado CONFIRMED")
    void shipOnlyFromConfirmed() {
        OrderLine line = new OrderLine("PRODUCT-1", 1, new BigDecimal("10.00"));
        Order order = Order.create("ORDER-1", List.of(line));

        assertThrows(InvalidOrderStateException.class, order::ship);

        order.confirm();
        order.ship();
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }

    @Test
    @DisplayName("No se puede cancelar un pedido en estado SHIPPED")
    void cannotCancelShippedOrder() {
        OrderLine line = new OrderLine("PRODUCT-1", 1, new BigDecimal("10.00"));
        Order order = Order.create("ORDER-1", List.of(line));

        order.confirm();
        order.ship();

        assertThrows(InvalidOrderStateException.class, order::cancel);
    }

    @Test
    @DisplayName("Se puede cancelar un pedido en estado CREATED o CONFIRMED")
    void canCancelFromCreatedOrConfirmed() {
        OrderLine line = new OrderLine("PRODUCT-1", 1, new BigDecimal("10.00"));

        Order createdOrder = Order.create("ORDER-1", List.of(line));
        createdOrder.cancel();
        assertEquals(OrderStatus.CANCELLED, createdOrder.getStatus());

        Order confirmedOrder = Order.create("ORDER-2", List.of(line));
        confirmedOrder.confirm();
        confirmedOrder.cancel();
        assertEquals(OrderStatus.CANCELLED, confirmedOrder.getStatus());
    }

    @Test
    @DisplayName("El total nunca es negativo")
    void totalIsNeverNegative() {
        OrderLine line = new OrderLine("PRODUCT-1", 1, new BigDecimal("0.00"));
        Order order = Order.create("ORDER-1", List.of(line));

        assertTrue(order.getTotal().compareTo(BigDecimal.ZERO) >= 0);
    }
}

