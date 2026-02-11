package com.example.management.infrastructure.adapters.out.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA para persistir pedidos en la base de datos.
 * Adaptador de salida que mapea el modelo de dominio Order a la estructura de base de datos.
 */
@Entity
@Table(name = "orders")
public class OrderEntity {
    
    @Id
    private String id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private com.example.management.domain.model.OrderStatus status;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal total;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineEntity> orderLines = new ArrayList<>();
    
    @Version
    private Long version;
    
    protected OrderEntity() {
        // Requerido por JPA
    }
    
    public OrderEntity(String id, com.example.management.domain.model.OrderStatus status, BigDecimal total) {
        this.id = id;
        this.status = status;
        this.total = total;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public com.example.management.domain.model.OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(com.example.management.domain.model.OrderStatus status) {
        this.status = status;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public List<OrderLineEntity> getOrderLines() {
        return orderLines;
    }
    
    public void setOrderLines(List<OrderLineEntity> orderLines) {
        this.orderLines = orderLines;
    }
    
    public Long getVersion() {
        return version;
    }
    
    public void setVersion(Long version) {
        this.version = version;
    }
}
