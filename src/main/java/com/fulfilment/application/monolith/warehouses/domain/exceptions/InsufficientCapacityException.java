package com.fulfilment.application.monolith.warehouses.domain.exceptions;

public class InsufficientCapacityException extends RuntimeException {
    public InsufficientCapacityException(String message) {
        super(message);
    }
}