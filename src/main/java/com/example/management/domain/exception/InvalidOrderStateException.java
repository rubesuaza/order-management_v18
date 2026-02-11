package com.example.management.domain.exception;

/**
 * Se lanza cuando se intenta realizar una transición de estado
 * no permitida sobre un agregado de dominio.
 */
public class InvalidOrderStateException extends DomainException {

    public InvalidOrderStateException(String message) {
        super(message);
    }
}

