package com.example.management.application.ports.in;

import com.example.management.application.dtos.OrderDto;

import java.util.Optional;

/**
 * Caso de uso para obtener un pedido por su identificador.
 */
public interface GetOrderUseCase {
    
    /**
     * Obtiene un pedido por su identificador.
     * 
     * @param orderId Identificador del pedido
     * @return Un Optional con el pedido si existe, vacío en caso contrario
     */
    Optional<OrderDto> getOrder(String orderId);
}
