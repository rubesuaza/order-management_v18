package com.example.management.domain.service;

import com.example.management.domain.model.Order;

import java.math.BigDecimal;

/**
 * Servicio de dominio para operaciones sobre pedidos.
 */
public class OrderDomainService {
    
    public BigDecimal calculateTotal(Order order) {
        return order.getTotal();
    }
}
