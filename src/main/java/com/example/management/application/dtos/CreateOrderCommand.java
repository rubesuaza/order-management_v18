package com.example.management.application.dtos;

import java.util.List;

/**
 * Comando de aplicación para crear un pedido.
 * El orderId se genera internamente en el caso de uso.
 */
public class CreateOrderCommand {
    
    private final List<OrderLineDto> orderLines;
    
    public CreateOrderCommand(List<OrderLineDto> orderLines) {
        this.orderLines = orderLines;
    }
    
    public List<OrderLineDto> getOrderLines() {
        return orderLines;
    }
}
