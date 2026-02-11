package com.example.management.domain.exception;

/**
 * Excepción base para todas las excepciones de dominio.
 *
 * Forma parte de la capa de dominio y no tiene dependencias
 * con frameworks externos.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}

