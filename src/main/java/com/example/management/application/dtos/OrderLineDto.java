package com.example.management.application.dtos;

import java.math.BigDecimal;

/**
 * DTO de aplicación para representar una línea de pedido.
 */
public class OrderLineDto {
    
    private final String productId;
    private final Integer quantity;
    private final BigDecimal unitPrice;
    
    public OrderLineDto(String productId, Integer quantity, BigDecimal unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
