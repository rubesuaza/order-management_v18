package com.example.management.application.ports.in;

import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderLine;

import java.util.List;

/**
 * Caso de uso para crear un nuevo pedido.
 */
public interface CreateOrderUseCase {
    
    /**
     * Crea un nuevo pedido.
     * 
     * @param orderId Identificador único del pedido
     * @param orderLines Líneas del pedido
     * @return El pedido creado
     */
    Order createOrder(String orderId, List<OrderLine> orderLines);
}
