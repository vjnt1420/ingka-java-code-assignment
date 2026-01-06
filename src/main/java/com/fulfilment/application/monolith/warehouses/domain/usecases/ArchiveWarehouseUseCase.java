package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

  private final WarehouseStore warehouseStore;

  @Inject
  public ArchiveWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void archive(Warehouse warehouse) {
    Log.info("Archiving warehouse with business unit code: " + warehouse.businessUnitCode);

    // Find the existing warehouse to archive
    Warehouse existingWarehouse = warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode);
    if (existingWarehouse == null) {
      Log.error("Warehouse to be archived not found with business unit code: " + warehouse.businessUnitCode);
      throw new RuntimeException("Warehouse with business unit code " + warehouse.businessUnitCode + " does not exist");
    }

    // Set the archivedAt timestamp to current time
    existingWarehouse.archivedAt = java.time.ZonedDateTime.now();

    Log.info("Successfully archived warehouse with business unit code: " + warehouse.businessUnitCode);

    warehouseStore.update(existingWarehouse);
  }
}
