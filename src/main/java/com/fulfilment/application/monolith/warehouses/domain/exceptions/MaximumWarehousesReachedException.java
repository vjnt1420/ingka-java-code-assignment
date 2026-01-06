package com.fulfilment.application.monolith.warehouses.domain.exceptions;

public class MaximumWarehousesReachedException extends RuntimeException {
    public MaximumWarehousesReachedException(String message) {
        super(message);
    }
}