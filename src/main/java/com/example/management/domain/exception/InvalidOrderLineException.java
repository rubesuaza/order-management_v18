package com.example.management.domain.exception;

/**
 * Thrown when an order line violates domain layer invariants.
 */
public class InvalidOrderLineException extends DomainException {

    public InvalidOrderLineException(String message) {
        super(message);
    }
}

