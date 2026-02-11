package com.example.management.infrastructure.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para la respuesta de un pedido.
 * Este DTO debe construirse desde OrderDto de la capa de aplicación,
 * no directamente desde entidades de dominio.
 */
public class OrderResponse {
    
    private String id;
    private String status;
    private BigDecimal total;
    private List<WebOrderLineDto> orderLines;
    
    public OrderResponse() {
    }
    
    public OrderResponse(String id, String status, BigDecimal total, List<WebOrderLineDto> orderLines) {
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
    
    public List<WebOrderLineDto> getOrderLines() {
        return orderLines;
    }
}
