package com.example.management.application.ports.in;

import com.example.management.domain.model.Order;

/**
 * Caso de uso para confirmar un pedido.
 */
public interface ConfirmOrderUseCase {
    
    /**
     * Confirma un pedido existente.
     * 
     * @param orderId Identificador del pedido a confirmar
     * @return El pedido confirmado
     * @throws com.example.management.domain.exception.InvalidOrderStateException si el pedido no está en estado CREATED
     */
    Order confirmOrder(String orderId);
}
