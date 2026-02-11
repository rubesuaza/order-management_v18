package com.example.management.domain.service;

import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderDomainServiceTest {

    @Test
    @DisplayName("El servicio de dominio calcula el total del pedido de forma consistente")
    void serviceCalculatesOrderTotalConsistently() {
        OrderLine line1 = new OrderLine("PRODUCT-1", 2, new BigDecimal("10.00"));
        OrderLine line2 = new OrderLine("PRODUCT-2", 1, new BigDecimal("5.50"));
        Order order = Order.create("ORDER-1", List.of(line1, line2));

        OrderDomainService service = new OrderDomainService();

        BigDecimal total = service.calculateTotal(order);

        assertEquals(new BigDecimal("25.50"), total);
        assertEquals(order.getTotal(), total);
    }
}

