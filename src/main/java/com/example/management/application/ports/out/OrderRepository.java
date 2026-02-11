package com.example.management.application.ports.out;

import com.example.management.domain.model.Order;

import java.util.Optional;

/**
 * Puerto de salida para persistencia de pedidos.
 * Define la interfaz que deben implementar los adaptadores de persistencia.
 */
public interface OrderRepository {
    
    /**
     * Guarda un pedido en el repositorio.
     * 
     * @param order El pedido a guardar
     * @return El pedido guardado
     */
    Order save(Order order);
    
    /**
     * Busca un pedido por su identificador.
     * 
     * @param orderId El identificador del pedido
     * @return Un Optional con el pedido si existe, vacío en caso contrario
     */
    Optional<Order> findById(String orderId);
    
    /**
     * Verifica si existe un pedido con el identificador dado.
     * 
     * @param orderId El identificador del pedido
     * @return true si existe, false en caso contrario
     */
    boolean existsById(String orderId);
}
