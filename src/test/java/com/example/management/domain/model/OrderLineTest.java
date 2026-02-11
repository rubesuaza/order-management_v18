package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidOrderLineException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderLineTest {

    @Test
    @DisplayName("Una línea de pedido válida calcula correctamente el subtotal")
    void validOrderLineShouldCalculateSubtotal() {
        OrderLine line = new OrderLine("PRODUCT-1", 2, new BigDecimal("10.50"));

        assertEquals(new BigDecimal("21.00"), line.getSubtotal());
    }

    @Test
    @DisplayName("La cantidad debe ser estrictamente positiva")
    void quantityMustBePositive() {
        assertThrows(InvalidOrderLineException.class,
                () -> new OrderLine("PRODUCT-1", 0, new BigDecimal("10.00")));

        assertThrows(InvalidOrderLineException.class,
                () -> new OrderLine("PRODUCT-1", -1, new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("El precio unitario no puede ser negativo")
    void unitPriceMustNotBeNegative() {
        assertThrows(InvalidOrderLineException.class,
                () -> new OrderLine("PRODUCT-1", 1, new BigDecimal("-0.01")));
    }

    @Test
    @DisplayName("El identificador de producto no puede ser nulo ni vacío")
    void productIdMustNotBeBlank() {
        assertThrows(InvalidOrderLineException.class,
                () -> new OrderLine(null, 1, new BigDecimal("10.00")));

        assertThrows(InvalidOrderLineException.class,
                () -> new OrderLine("   ", 1, new BigDecimal("10.00")));
    }
}

