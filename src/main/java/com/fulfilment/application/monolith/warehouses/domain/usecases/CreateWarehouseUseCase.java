package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseAlreadyExistsException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.InvalidLocationException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.MaximumWarehousesReachedException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.CapacityExceededException;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  @Inject
  public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void create(Warehouse warehouse) {
    Log.info("Creating warehouse with business unit code: " + warehouse.businessUnitCode);

    // Business Unit Code Verification: Ensure that the specified business unit code doesn't already exist
    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
      Log.warn("Warehouse with business unit code " + warehouse.businessUnitCode + " already exists");
      throw new WarehouseAlreadyExistsException(
          "Warehouse with business unit code " + warehouse.businessUnitCode + " already exists");
    }

    // Location Validation: Confirm that the warehouse location is valid
    Location location = locationResolver.resolveByIdentifier(warehouse.location);
    if (location == null) {
      Log.warn("Invalid location: " + warehouse.location);
      throw new InvalidLocationException("Location " + warehouse.location + " is not valid");
    }

    // Warehouse Creation Feasibility: Check if maximum number of warehouses has been reached
    List<Warehouse> existingWarehouses = warehouseStore.findByLocation(warehouse.location);
    if (existingWarehouses.size() >= location.maxNumberOfWarehouses) {
      Log.warn("Maximum number of warehouses reached for location: " + warehouse.location);
      throw new MaximumWarehousesReachedException(
          "Maximum number of warehouses reached for location: " + warehouse.location);
    }

    // Capacity and Stock Validation: Validate warehouse capacity
    int totalCapacityInLocation = existingWarehouses.stream()
        .mapToInt(w -> w.capacity != null ? w.capacity : 0)
        .sum();

    if (totalCapacityInLocation + warehouse.capacity > location.maxCapacity) {
      Log.warn("Capacity would exceed maximum for location: " + warehouse.location);
      throw new CapacityExceededException(
          "Adding warehouse with capacity " + warehouse.capacity +
          " would exceed maximum capacity of " + location.maxCapacity +
          " for location " + warehouse.location);
    }

    // Set creation timestamp
    warehouse.creationAt = java.time.ZonedDateTime.now();

    Log.info("Successfully created warehouse with business unit code: " + warehouse.businessUnitCode);

    // if all went well, create the warehouse
    warehouseStore.create(warehouse);
  }
}
