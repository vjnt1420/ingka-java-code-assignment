package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Override
  public void create(Warehouse warehouse) {
    Log.info("Creating warehouse in database with business unit code: " + warehouse.businessUnitCode);

    DbWarehouse dbWarehouse = new DbWarehouse();
    dbWarehouse.businessUnitCode = warehouse.businessUnitCode;
    dbWarehouse.location = warehouse.location;
    dbWarehouse.capacity = warehouse.capacity;
    dbWarehouse.stock = warehouse.stock;
    dbWarehouse.createdAt = warehouse.creationAt != null ?
        warehouse.creationAt.toLocalDateTime() : LocalDateTime.now();
    dbWarehouse.archivedAt = warehouse.archivedAt != null ?
        warehouse.archivedAt.toLocalDateTime() : null;

    persist(dbWarehouse);

    Log.info("Successfully created warehouse in database with business unit code: " + warehouse.businessUnitCode);
  }

  @Override
  public void update(Warehouse warehouse) {
    Log.info("Updating warehouse in database with business unit code: " + warehouse.businessUnitCode);

    DbWarehouse dbWarehouse = find("businessUnitCode", warehouse.businessUnitCode).firstResult();
    if (dbWarehouse != null) {
      dbWarehouse.location = warehouse.location;
      dbWarehouse.capacity = warehouse.capacity;
      dbWarehouse.stock = warehouse.stock;
      dbWarehouse.archivedAt = warehouse.archivedAt != null ?
          warehouse.archivedAt.toLocalDateTime() : null;

      persist(dbWarehouse);
      Log.info("Successfully updated warehouse in database with business unit code: " + warehouse.businessUnitCode);
    } else {
      Log.error("Warehouse not found for update with business unit code: " + warehouse.businessUnitCode);
    }
  }

  @Override
  public void remove(Warehouse warehouse) {
    Log.info("Removing warehouse from database with business unit code: " + warehouse.businessUnitCode);

    DbWarehouse dbWarehouse = find("businessUnitCode", warehouse.businessUnitCode).firstResult();
    if (dbWarehouse != null) {
      delete(dbWarehouse);
      Log.info("Successfully removed warehouse from database with business unit code: " + warehouse.businessUnitCode);
    } else {
      Log.error("Warehouse not found for removal with business unit code: " + warehouse.businessUnitCode);
    }
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    Log.debug("Finding warehouse by business unit code: " + buCode);

    DbWarehouse dbWarehouse = find("businessUnitCode", buCode).firstResult();
    if (dbWarehouse != null) {
      return mapToDomain(dbWarehouse);
    }
    Log.debug("Warehouse not found with business unit code: " + buCode);
    return null;
  }

  @Override
  public List<Warehouse> findByLocation(String location) {
    Log.debug("Finding warehouses by location: " + location);

    List<DbWarehouse> dbWarehouses = find("location", location).list();
    return dbWarehouses.stream()
        .map(this::mapToDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Warehouse> findAllWarehouses() {
    Log.debug("Finding all warehouses");

    List<DbWarehouse> dbWarehouses = listAll(Sort.by("id"));
    return dbWarehouses.stream()
        .map(this::mapToDomain)
        .collect(Collectors.toList());
  }

  private Warehouse mapToDomain(DbWarehouse dbWarehouse) {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = dbWarehouse.businessUnitCode;
    warehouse.location = dbWarehouse.location;
    warehouse.capacity = dbWarehouse.capacity;
    warehouse.stock = dbWarehouse.stock;
    warehouse.creationAt = dbWarehouse.createdAt != null ?
        dbWarehouse.createdAt.atZone(java.time.ZoneId.systemDefault()) : null;
    warehouse.archivedAt = dbWarehouse.archivedAt != null ?
        dbWarehouse.archivedAt.atZone(java.time.ZoneId.systemDefault()) : null;

    return warehouse;
  }
}
