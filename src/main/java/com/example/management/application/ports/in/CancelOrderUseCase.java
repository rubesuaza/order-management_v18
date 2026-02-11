package com.example.management.application.ports.in;

import com.example.management.application.dtos.OrderDto;

/**
 * Caso de uso para cancelar un pedido.
 */
public interface CancelOrderUseCase {
    
    /**
     * Cancela un pedido existente.
     * 
     * @param orderId Identificador del pedido a cancelar
     * @return El pedido cancelado
     * @throws com.example.management.domain.exception.InvalidOrderStateException si el pedido está en estado SHIPPED
     */
    OrderDto cancelOrder(String orderId);
}
