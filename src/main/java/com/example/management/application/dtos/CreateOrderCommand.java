package com.example.management.application.dtos;

import java.util.List;

/**
 * Comando de aplicación para crear un pedido.
 */
public class CreateOrderCommand {
    
    private final String orderId;
    private final List<OrderLineDto> orderLines;
    
    public CreateOrderCommand(String orderId, List<OrderLineDto> orderLines) {
        this.orderId = orderId;
        this.orderLines = orderLines;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public List<OrderLineDto> getOrderLines() {
        return orderLines;
    }
}
