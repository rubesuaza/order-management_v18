package com.example.management.domain.exception;

/**
 * Se lanza cuando una línea de pedido viola las invariantes
 * de la capa de dominio.
 */
public class InvalidOrderLineException extends DomainException {

    public InvalidOrderLineException(String message) {
        super(message);
    }
}

