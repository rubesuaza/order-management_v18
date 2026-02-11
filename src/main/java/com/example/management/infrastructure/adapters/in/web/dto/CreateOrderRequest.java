package com.example.management.infrastructure.adapters.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * DTO para la petición de creación de un pedido.
 */
public class CreateOrderRequest {
    
    @NotEmpty(message = "El pedido debe tener al menos una línea")
    @Valid
    private List<OrderLineDto> orderLines;
    
    public CreateOrderRequest() {
    }
    
    public CreateOrderRequest(List<OrderLineDto> orderLines) {
        this.orderLines = orderLines;
    }
    
    public List<OrderLineDto> getOrderLines() {
        return orderLines;
    }
    
    public void setOrderLines(List<OrderLineDto> orderLines) {
        this.orderLines = orderLines;
    }
}
