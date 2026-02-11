package com.example.management.domain.exception;

/**
 * Se lanza cuando un pedido completo viola una o más
 * reglas de validación de dominio.
 */
public class OrderValidationException extends DomainException {

    public OrderValidationException(String message) {
        super(message);
    }
}

