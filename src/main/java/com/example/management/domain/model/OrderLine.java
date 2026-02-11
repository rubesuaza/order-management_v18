package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidOrderLineException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Línea de pedido que representa un producto con cantidad y precio unitario.
 */
public class OrderLine {
    
    private final String productId;
    private final int quantity;
    private final BigDecimal unitPrice;
    
    public OrderLine(String productId, int quantity, BigDecimal unitPrice) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new InvalidOrderLineException("El identificador de producto no puede ser nulo ni vacío");
        }
        if (quantity <= 0) {
            throw new InvalidOrderLineException("La cantidad debe ser estrictamente positiva");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidOrderLineException("El precio unitario no puede ser negativo");
        }
        
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLine orderLine = (OrderLine) o;
        return quantity == orderLine.quantity &&
                Objects.equals(productId, orderLine.productId) &&
                Objects.equals(unitPrice, orderLine.unitPrice);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productId, quantity, unitPrice);
    }
}
