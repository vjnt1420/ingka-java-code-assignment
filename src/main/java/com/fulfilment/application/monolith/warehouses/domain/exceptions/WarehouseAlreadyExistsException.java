package com.fulfilment.application.monolith.warehouses.domain.exceptions;

public class WarehouseAlreadyExistsException extends RuntimeException {
    public WarehouseAlreadyExistsException(String message) {
        super(message);
    }
}