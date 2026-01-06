package com.fulfilment.application.monolith.warehouses.domain.exceptions;

public class InvalidLocationException extends RuntimeException {
    public InvalidLocationException(String message) {
        super(message);
    }
}