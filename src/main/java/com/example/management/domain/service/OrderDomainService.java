package com.example.management.domain.service;

/**
 * Servicio de dominio para operaciones de negocio que involucran el agregado Order.
 * Encapsula lógica de negocio que no pertenece naturalmente al agregado mismo.
 * 
 * Actualmente vacío ya que toda la lógica de negocio relacionada con cambios de estado
 * del agregado Order reside dentro del propio agregado.
 */
public class OrderDomainService {
    // Los métodos confirmOrder, shipOrder y cancelOrder fueron removidos
    // ya que la lógica de negocio reside directamente en el agregado Order.
}
