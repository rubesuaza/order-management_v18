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
    @DisplayName("A valid order is created in CREATED state and with correct total")
    void validOrderIsCreatedWithCorrectTotal() {
        OrderLine line1 = new OrderLine("PRODUCT-1", 2, new BigDecimal("10.00"));
        OrderLine line2 = new OrderLine("PRODUCT-2", 1, new BigDecimal("5.50"));

        Order order = Order.create("ORDER-1", List.of(line1, line2));

        assertEquals("ORDER-1", order.getId());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(new BigDecimal("25.50"), order.getTotal());
    }

    @Test
    @DisplayName("An order must have at least one line")
    void orderMustHaveAtLeastOneLine() {
        assertThrows(OrderValidationException.class,
                () -> Order.create("ORDER-1", List.of()));
    }

    @Test
    @DisplayName("Order identifier cannot be null or empty")
    void orderIdMustNotBeBlank() {
        OrderLine line = new OrderLine("PRODUCT-1", 1, new BigDecimal("10.00"));

        assertThrows(OrderValidationException.class,
                () -> Order.create(null, List.of(line)));

        assertThrows(OrderValidationException.class,
                () -> Order.create("   ", List.of(line)));
    }

    @Test
    @DisplayName("Null lines are not allowed in the order")
    void orderMustNotContainNullLines() {
        OrderLine line = new OrderLine("PRODUCT-1", 1, new BigDecimal("10.00"));

        assertThrows(OrderValidationException.class,
                () -> Order.create("ORDER-1", Arrays.asList(line, null)));
    }

    @Test
    @DisplayName("An order can only be confirmed when in CREATED state")
    void confirmOnlyFromCreated() {
        OrderLine line = new OrderLine("PRODUCT-1", 1, new BigDecimal("10.00"));
        Order order = Order.create("ORDER-1", List.of(line));

        order.confirm();
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());

        assertThrows(InvalidOrderStateException.class, order::confirm);
    }

    @Test
    @DisplayName("An order can only be shipped when in CONFIRMED state")
    void shipOnlyFromConfirmed() {
        OrderLine line = new OrderLine("PRODUCT-1", 1, new BigDecimal("10.00"));
        Order order = Order.create("ORDER-1", List.of(line));

        assertThrows(InvalidOrderStateException.class, order::ship);

        order.confirm();
        order.ship();
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }

    @Test
    @DisplayName("An order in SHIPPED state cannot be cancelled")
    void cannotCancelShippedOrder() {
        OrderLine line = new OrderLine("PRODUCT-1", 1, new BigDecimal("10.00"));
        Order order = Order.create("ORDER-1", List.of(line));

        order.confirm();
        order.ship();

        assertThrows(InvalidOrderStateException.class, order::cancel);
    }

    @Test
    @DisplayName("An order can be cancelled when in CREATED or CONFIRMED state")
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
    @DisplayName("Total is never negative")
    void totalIsNeverNegative() {
        OrderLine line = new OrderLine("PRODUCT-1", 1, new BigDecimal("0.00"));
        Order order = Order.create("ORDER-1", List.of(line));

        assertTrue(order.getTotal().compareTo(BigDecimal.ZERO) >= 0);
    }
}

