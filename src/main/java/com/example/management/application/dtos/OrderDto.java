package com.example.management.application.dtos;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de aplicación para representar un pedido.
 * Usado para transferir datos entre la capa de aplicación y los adaptadores.
 */
public class OrderDto {
    
    private final String id;
    private final String status;
    private final BigDecimal total;
    private final List<OrderLineDto> orderLines;
    
    public OrderDto(String id, String status, BigDecimal total, List<OrderLineDto> orderLines) {
        this.id = id;
        this.status = status;
        this.total = total;
        this.orderLines = orderLines;
    }
    
    public String getId() {
        return id;
    }
    
    public String getStatus() {
        return status;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public List<OrderLineDto> getOrderLines() {
        return orderLines;
    }
}
