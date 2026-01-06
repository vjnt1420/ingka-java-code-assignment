# Postman API Testing Document

This document provides detailed instructions for testing all API endpoints using Postman.

## Base URL
`http://localhost:9090`

## Warehouse API Endpoints

### 1. List All Warehouses
- **Method**: GET
- **URL**: `/warehouse`
- **Headers**: 
  - Content-Type: application/json
- **Description**: Retrieves a list of all warehouses
- **Sample Request**:
  ```
  GET http://localhost:9090/warehouse
  Headers:
  Content-Type: application/json
  ```
- **Expected Response**: Array of warehouse objects

### 2. Create a New Warehouse
- **Method**: POST
- **URL**: `/warehouse`
- **Headers**: 
  - Content-Type: application/json
- **Description**: Creates a new warehouse with validation
- **Sample Request**:
  ```
  POST http://localhost:9090/warehouse
  Headers:
  Content-Type: application/json
  
  Body (JSON):
  {
    "id": "WH001",
    "location": "AMSTERDAM-001",
    "capacity": 20,
    "stock": 10
  }
  ```
- **Expected Response**: Created warehouse object
- **Validation Notes**:
  - Business unit code must be unique
  - Location must be valid (from predefined locations)
  - Warehouse count must not exceed location's maxNumberOfWarehouses
  - Capacity must not exceed location's maxCapacity

### 3. Get a Warehouse by ID
- **Method**: GET
- **URL**: `/warehouse/{id}`
- **Headers**: 
  - Content-Type: application/json
- **Path Variable**: 
  - id: The warehouse ID to retrieve
- **Description**: Retrieves a specific warehouse by its ID
- **Sample Request**:
  ```
  GET http://localhost:9090/warehouse/WH001
  Headers:
  Content-Type: application/json
  ```
- **Expected Response**: Single warehouse object or 404 if not found

### 4. Archive a Warehouse by ID
- **Method**: DELETE
- **URL**: `/warehouse/{id}`
- **Headers**: 
  - Content-Type: application/json
- **Path Variable**: 
  - id: The warehouse ID to archive
- **Description**: Archives a warehouse by setting its archivedAt timestamp
- **Sample Request**:
  ```
  DELETE http://localhost:9090/warehouse/WH001
  Headers:
  Content-Type: application/json
  ```
- **Expected Response**: 204 No Content on success, or error if warehouse doesn't exist

## Store API Endpoints

### 1. List All Stores
- **Method**: GET
- **URL**: `/stores`
- **Headers**: 
  - Content-Type: application/json
- **Description**: Retrieves a list of all stores
- **Sample Request**:
  ```
  GET http://localhost:9090/stores
  Headers:
  Content-Type: application/json
  ```
- **Expected Response**: Array of store objects

### 2. Get a Store by ID
- **Method**: GET
- **URL**: `/stores/{id}`
- **Headers**: 
  - Content-Type: application/json
- **Path Variable**: 
  - id: The store ID to retrieve
- **Description**: Retrieves a specific store by its ID
- **Sample Request**:
  ```
  GET http://localhost:9090/stores/1
  Headers:
  Content-Type: application/json
  ```
- **Expected Response**: Single store object or 404 if not found

### 3. Create a New Store
- **Method**: POST
- **URL**: `/stores`
- **Headers**: 
  - Content-Type: application/json
- **Description**: Creates a new store and synchronizes with legacy system after transaction commit
- **Sample Request**:
  ```
  POST http://localhost:9090/stores
  Headers:
  Content-Type: application/json
  
  Body (JSON):
  {
    "name": "NEW_STORE",
    "quantityProductsInStock": 25
  }
  ```
- **Expected Response**: Created store object with 201 status
- **Note**: The legacy system will be notified after the database transaction commits

### 4. Update a Store
- **Method**: PUT
- **URL**: `/stores/{id}`
- **Headers**: 
  - Content-Type: application/json
- **Path Variable**: 
  - id: The store ID to update
- **Description**: Updates an existing store and synchronizes with legacy system after transaction commit
- **Sample Request**:
  ```
  PUT http://localhost:9090/stores/1
  Headers:
  Content-Type: application/json
  
  Body (JSON):
  {
    "name": "UPDATED_STORE_NAME",
    "quantityProductsInStock": 30
  }
  ```
- **Expected Response**: Updated store object

### 5. Partial Update a Store
- **Method**: PATCH
- **URL**: `/stores/{id}`
- **Headers**: 
  - Content-Type: application/json
- **Path Variable**: 
  - id: The store ID to update
- **Description**: Partially updates an existing store and synchronizes with legacy system after transaction commit
- **Sample Request**:
  ```
  PATCH http://localhost:9090/stores/1
  Headers:
  Content-Type: application/json
  
  Body (JSON):
  {
    "name": "PARTIALLY_UPDATED_STORE",
    "quantityProductsInStock": 35
  }
  ```
- **Expected Response**: Updated store object

### 6. Delete a Store
- **Method**: DELETE
- **URL**: `/stores/{id}`
- **Headers**: 
  - Content-Type: application/json
- **Path Variable**: 
  - id: The store ID to delete
- **Description**: Deletes a store from the database
- **Sample Request**:
  ```
  DELETE http://localhost:9090/stores/1
  Headers:
  Content-Type: application/json
  ```
- **Expected Response**: 204 No Content on success

## Product API Endpoints

### 1. List All Products
- **Method**: GET
- **URL**: `/product`
- **Headers**: 
  - Content-Type: application/json
- **Description**: Retrieves a list of all products
- **Sample Request**:
  ```
  GET http://localhost:9090/product
  Headers:
  Content-Type: application/json
  ```
- **Expected Response**: Array of product objects ordered by name

### 2. Get a Product by ID
- **Method**: GET
- **URL**: `/product/{id}`
- **Headers**: 
  - Content-Type: application/json
- **Path Variable**: 
  - id: The product ID to retrieve
- **Description**: Retrieves a specific product by its ID
- **Sample Request**:
  ```
  GET http://localhost:9090/product/1
  Headers:
  Content-Type: application/json
  ```
- **Expected Response**: Single product object or 404 if not found

### 3. Create a New Product
- **Method**: POST
- **URL**: `/product`
- **Headers**: 
  - Content-Type: application/json
- **Description**: Creates a new product
- **Sample Request**:
  ```
  POST http://localhost:9090/product
  Headers:
  Content-Type: application/json
  
  Body (JSON):
  {
    "name": "NEW_PRODUCT",
    "stock": 50,
    "price": 29.99
  }
  ```
- **Expected Response**: Created product object with 201 status

### 4. Update a Product
- **Method**: PUT
- **URL**: `/product/{id}`
- **Headers**: 
  - Content-Type: application/json
- **Path Variable**: 
  - id: The product ID to update
- **Description**: Updates an existing product
- **Sample Request**:
  ```
  PUT http://localhost:9090/product/1
  Headers:
  Content-Type: application/json
  
  Body (JSON):
  {
    "name": "UPDATED_PRODUCT_NAME",
    "stock": 60,
    "price": 34.99
  }
  ```
- **Expected Response**: Updated product object

### 5. Delete a Product
- **Method**: DELETE
- **URL**: `/product/{id}`
- **Headers**: 
  - Content-Type: application/json
- **Path Variable**: 
  - id: The product ID to delete
- **Description**: Deletes a product from the database
- **Sample Request**:
  ```
  DELETE http://localhost:9090/product/1
  Headers:
  Content-Type: application/json
  ```
- **Expected Response**: 204 No Content on success

## Predefined Locations for Warehouse Testing

Use these location identifiers when creating warehouses:

1. "ZWOLLE-001" - Max warehouses: 1, Max capacity: 40
2. "ZWOLLE-002" - Max warehouses: 2, Max capacity: 50
3. "AMSTERDAM-001" - Max warehouses: 5, Max capacity: 100
4. "AMSTERDAM-002" - Max warehouses: 3, Max capacity: 75
5. "TILBURG-001" - Max warehouses: 1, Max capacity: 40
6. "HELMOND-001" - Max warehouses: 1, Max capacity: 45
7. "EINDHOVEN-001" - Max warehouses: 2, Max capacity: 70
8. "VETSBY-001" - Max warehouses: 1, Max capacity: 90

## Common Error Responses

The application returns standardized error responses in the format:
```json
{
  "exceptionType": "ExceptionClassName",
  "code": 500,
  "error": "Error message"
}
```

## Testing Sequence Recommendation

1. Start by testing the Store API endpoints
2. Test the Product API endpoints
3. Test the Warehouse API endpoints (note that some locations have limited capacity)
4. Verify that the legacy system integration works by checking logs after Store operations
5. Test validation rules by attempting to create warehouses that violate business rules

## Expected Response Codes

- 200: Success for GET requests
- 201: Success for POST requests (resource created)
- 204: Success for DELETE requests (no content)
- 404: Resource not found
- 422: Validation error (unprocessable entity)
- 500: Internal server error