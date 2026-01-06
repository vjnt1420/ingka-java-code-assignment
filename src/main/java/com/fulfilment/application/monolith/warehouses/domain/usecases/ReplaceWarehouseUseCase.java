package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.InsufficientCapacityException;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  @Inject
  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void replace(Warehouse newWarehouse) {
    Log.info("Replacing warehouse with business unit code: " + newWarehouse.businessUnitCode);

    // Find the existing warehouse that is being replaced
    Warehouse existingWarehouse = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);
    if (existingWarehouse == null) {
      Log.error("Warehouse to be replaced not found with business unit code: " + newWarehouse.businessUnitCode);
      throw new RuntimeException("Warehouse with business unit code " + newWarehouse.businessUnitCode + " does not exist");
    }

    // Location Validation: Confirm that the warehouse location is valid
    Location location = locationResolver.resolveByIdentifier(newWarehouse.location);
    if (location == null) {
      Log.warn("Invalid location: " + newWarehouse.location);
      throw new RuntimeException("Location " + newWarehouse.location + " is not valid");
    }

    // Capacity Accommodation: Ensure the new warehouse's capacity can accommodate the stock from the warehouse being replaced
    if (newWarehouse.capacity < existingWarehouse.stock) {
      Log.warn("New warehouse capacity " + newWarehouse.capacity + " is less than existing warehouse stock " + existingWarehouse.stock);
      throw new InsufficientCapacityException(
          "New warehouse capacity " + newWarehouse.capacity +
          " cannot accommodate the stock " + existingWarehouse.stock +
          " from the warehouse being replaced");
    }

    // Stock Matching: Confirm that the stock of the new warehouse matches the stock of the previous warehouse
    // In this case, we're assuming the new warehouse should have at least the same stock as the old one
    if (newWarehouse.stock < existingWarehouse.stock) {
      Log.warn("New warehouse stock " + newWarehouse.stock + " is less than existing warehouse stock " + existingWarehouse.stock);
      throw new RuntimeException(
          "New warehouse stock " + newWarehouse.stock +
          " should match or exceed the stock " + existingWarehouse.stock +
          " of the warehouse being replaced");
    }

    // Check if the new capacity would exceed the location's maximum capacity
    List<Warehouse> warehousesInLocation = warehouseStore.findByLocation(newWarehouse.location);
    int totalCapacityInLocation = warehousesInLocation.stream()
        .filter(w -> !w.businessUnitCode.equals(existingWarehouse.businessUnitCode)) // Exclude the warehouse being replaced
        .mapToInt(w -> w.capacity != null ? w.capacity : 0)
        .sum();

    if (totalCapacityInLocation + newWarehouse.capacity > location.maxCapacity) {
      Log.warn("New warehouse capacity would exceed maximum for location: " + newWarehouse.location);
      throw new RuntimeException(
          "New warehouse with capacity " + newWarehouse.capacity +
          " would exceed maximum capacity of " + location.maxCapacity +
          " for location " + newWarehouse.location);
    }

    // Update the warehouse with new values
    existingWarehouse.location = newWarehouse.location;
    existingWarehouse.capacity = newWarehouse.capacity;
    existingWarehouse.stock = newWarehouse.stock;

    Log.info("Successfully replaced warehouse with business unit code: " + newWarehouse.businessUnitCode);

    warehouseStore.update(existingWarehouse);
  }
}
