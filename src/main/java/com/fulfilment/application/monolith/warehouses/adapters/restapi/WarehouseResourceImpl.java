package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
public class WarehouseResourceImpl implements WarehouseResource {

  @Inject
  CreateWarehouseOperation createWarehouseOperation;

  @Inject
  ArchiveWarehouseOperation archiveWarehouseOperation;

  @Inject
  WarehouseStore warehouseStore;

  @Override
  public List<com.warehouse.api.beans.Warehouse> listAllWarehousesUnits() {
    Log.info("Listing all warehouse units");

    List<com.fulfilment.application.monolith.warehouses.domain.models.Warehouse> domainWarehouses = warehouseStore.findAllWarehouses();

    return domainWarehouses.stream()
        .map(domainWarehouse -> {
          com.warehouse.api.beans.Warehouse apiWarehouse = new com.warehouse.api.beans.Warehouse();
          apiWarehouse.setId(domainWarehouse.businessUnitCode);
          apiWarehouse.setLocation(domainWarehouse.location);
          apiWarehouse.setCapacity(domainWarehouse.capacity);
          apiWarehouse.setStock(domainWarehouse.stock);
          return apiWarehouse;
        })
        .collect(Collectors.toList());
  }

  @Override
  public com.warehouse.api.beans.Warehouse createANewWarehouseUnit(@NotNull com.warehouse.api.beans.Warehouse data) {
    Log.info("Creating a new warehouse unit with business unit code: " + data.getId());

    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse domainWarehouse = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    domainWarehouse.businessUnitCode = data.getId();
    domainWarehouse.location = data.getLocation();
    domainWarehouse.capacity = data.getCapacity();
    domainWarehouse.stock = data.getStock();

    createWarehouseOperation.create(domainWarehouse);

    Log.info("Successfully created warehouse unit with business unit code: " + data.getId());
    return data;
  }

  @Override
  public com.warehouse.api.beans.Warehouse getAWarehouseUnitByID(String id) {
    Log.info("Getting warehouse unit by ID: " + id);

    // Find the warehouse using the warehouse store
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse domainWarehouse = warehouseStore.findByBusinessUnitCode(id);
    if (domainWarehouse == null) {
      Log.warn("Warehouse not found with business unit code: " + id);
      throw new WebApplicationException("Warehouse with business unit code " + id + " does not exist.", 404);
    }

    // Convert domain warehouse to API warehouse
    com.warehouse.api.beans.Warehouse warehouse = new com.warehouse.api.beans.Warehouse();
    warehouse.setId(domainWarehouse.businessUnitCode);
    warehouse.setLocation(domainWarehouse.location);
    warehouse.setCapacity(domainWarehouse.capacity);
    warehouse.setStock(domainWarehouse.stock);

    Log.info("Successfully retrieved warehouse unit by ID: " + id);
    return warehouse;
  }

  @Override
  public void archiveAWarehouseUnitByID(String id) {
    Log.info("Archiving warehouse unit by ID: " + id);

    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse domainWarehouse = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    domainWarehouse.businessUnitCode = id;

    archiveWarehouseOperation.archive(domainWarehouse);

    Log.info("Successfully archived warehouse unit with ID: " + id);
  }
}
