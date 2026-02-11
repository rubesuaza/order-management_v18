package com.example.management.domain.service;

import com.example.management.domain.model.Order;

/**
 * Servicio de dominio para operaciones de negocio que involucran el agregado Order.
 * Encapsula lógica de negocio que no pertenece naturalmente al agregado mismo.
 */
public class OrderDomainService {
    
    /**
     * Confirma un pedido aplicando las reglas de negocio correspondientes.
     * 
     * @param order El pedido a confirmar
     * @return El pedido confirmado
     */
    public Order confirmOrder(Order order) {
        order.confirm();
        return order;
    }
    
    /**
     * Envía un pedido aplicando las reglas de negocio correspondientes.
     * 
     * @param order El pedido a enviar
     * @return El pedido enviado
     */
    public Order shipOrder(Order order) {
        order.ship();
        return order;
    }
    
    /**
     * Cancela un pedido aplicando las reglas de negocio correspondientes.
     * 
     * @param order El pedido a cancelar
     * @return El pedido cancelado
     */
    public Order cancelOrder(Order order) {
        order.cancel();
        return order;
    }
}
