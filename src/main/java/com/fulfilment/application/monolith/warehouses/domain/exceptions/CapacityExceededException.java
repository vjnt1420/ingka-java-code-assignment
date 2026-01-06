package com.fulfilment.application.monolith.warehouses.domain.exceptions;

public class CapacityExceededException extends RuntimeException {
    public CapacityExceededException(String message) {
        super(message);
    }
}