package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidOrderStateException;
import com.example.management.domain.exception.OrderValidationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Agregado raíz que representa un pedido en el dominio.
 */
public class Order {
    
    private String id;
    private OrderStatus status;
    private List<OrderLine> orderLines;
    private BigDecimal total;
    
    private Order(String id, List<OrderLine> orderLines) {
        if (id == null || id.trim().isEmpty()) {
            throw new OrderValidationException("El identificador de pedido no puede ser nulo ni vacío");
        }
        if (orderLines == null || orderLines.isEmpty()) {
            throw new OrderValidationException("Un pedido debe tener al menos una línea");
        }
        if (orderLines.stream().anyMatch(Objects::isNull)) {
            throw new OrderValidationException("No se permiten líneas nulas en el pedido");
        }
        
        this.id = id;
        this.status = OrderStatus.CREATED;
        this.orderLines = new ArrayList<>(orderLines);
        this.total = calculateTotal();
    }
    
    public static Order create(String id, List<OrderLine> orderLines) {
        return new Order(id, orderLines);
    }
    
    /**
     * Reconstruye un Order desde persistencia con un estado específico.
     * Este método permite reconstruir el agregado sin ejecutar transiciones de estado,
     * usado por adaptadores de persistencia para restaurar el estado exacto guardado.
     * 
     * @param id Identificador del pedido
     * @param orderLines Líneas del pedido
     * @param status Estado del pedido
     * @return Order reconstruido con el estado especificado
     */
    public static Order reconstruct(String id, List<OrderLine> orderLines, OrderStatus status) {
        if (id == null || id.trim().isEmpty()) {
            throw new OrderValidationException("El identificador de pedido no puede ser nulo ni vacío");
        }
        if (orderLines == null || orderLines.isEmpty()) {
            throw new OrderValidationException("Un pedido debe tener al menos una línea");
        }
        if (orderLines.stream().anyMatch(Objects::isNull)) {
            throw new OrderValidationException("No se permiten líneas nulas en el pedido");
        }
        if (status == null) {
            throw new OrderValidationException("El estado del pedido no puede ser nulo");
        }
        
        Order order = new Order(id, orderLines);
        order.status = status; // Asignación directa sin validación de transición
        return order;
    }
    
    public void confirm() {
        if (this.status != OrderStatus.CREATED) {
            throw new InvalidOrderStateException("Solo se puede confirmar un pedido en estado CREATED");
        }
        this.status = OrderStatus.CONFIRMED;
    }
    
    public void ship() {
        if (this.status != OrderStatus.CONFIRMED) {
            throw new InvalidOrderStateException("Solo se puede enviar un pedido en estado CONFIRMED");
        }
        this.status = OrderStatus.SHIPPED;
    }
    
    public void cancel() {
        if (this.status == OrderStatus.SHIPPED) {
            throw new InvalidOrderStateException("No se puede cancelar un pedido en estado SHIPPED");
        }
        this.status = OrderStatus.CANCELLED;
    }
    
    private BigDecimal calculateTotal() {
        return orderLines.stream()
                .map(OrderLine::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public String getId() {
        return id;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public List<OrderLine> getOrderLines() {
        return Collections.unmodifiableList(orderLines);
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
