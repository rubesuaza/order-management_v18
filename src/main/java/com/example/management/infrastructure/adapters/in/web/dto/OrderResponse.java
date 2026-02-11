package com.example.management.infrastructure.adapters.in.web.dto;

import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderLine;
import com.example.management.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO para la respuesta de un pedido.
 */
public class OrderResponse {
    
    private String id;
    private String status;
    private BigDecimal total;
    private List<OrderLineDto> orderLines;
    
    public OrderResponse() {
    }
    
    public OrderResponse(String id, String status, BigDecimal total, List<OrderLineDto> orderLines) {
        this.id = id;
        this.status = status;
        this.total = total;
        this.orderLines = orderLines;
    }
    
    public static OrderResponse fromDomain(Order order) {
        List<OrderLineDto> orderLineDtos = order.getOrderLines().stream()
                .map(line -> new OrderLineDto(
                        line.getProductId(),
                        line.getQuantity(),
                        line.getUnitPrice()
                ))
                .collect(Collectors.toList());
        
        return new OrderResponse(
                order.getId(),
                order.getStatus().name(),
                order.getTotal(),
                orderLineDtos
        );
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
