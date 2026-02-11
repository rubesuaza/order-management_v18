package com.example.management.application.ports.in;

import com.example.management.application.dtos.CreateOrderCommand;
import com.example.management.application.dtos.OrderDto;

/**
 * Caso de uso para crear un nuevo pedido.
 */
public interface CreateOrderUseCase {
    
    /**
     * Crea un nuevo pedido.
     * 
     * @param command Comando con los datos del pedido a crear
     * @return El pedido creado
     */
    OrderDto createOrder(CreateOrderCommand command);
}
