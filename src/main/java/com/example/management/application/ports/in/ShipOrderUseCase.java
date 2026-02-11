package com.example.management.application.ports.in;

import com.example.management.domain.model.Order;

/**
 * Caso de uso para enviar un pedido.
 */
public interface ShipOrderUseCase {
    
    /**
     * Envía un pedido existente.
     * 
     * @param orderId Identificador del pedido a enviar
     * @return El pedido enviado
     * @throws com.example.management.domain.exception.InvalidOrderStateException si el pedido no está en estado CONFIRMED
     */
    Order shipOrder(String orderId);
}
