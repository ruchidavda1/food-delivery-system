# RoofTop Food Delivery System - Complete Documentation

## Table of Contents
1. [Overview](#overview)
2. [System Architecture](#system-architecture)
3. [Algorithm Explanation](#algorithm-explanation)
4. [API Documentation](#api-documentation)
5. [Database Schema](#database-schema)
6. [Screenshots](#screenshots)
7. [Example Walkthrough](#example-walkthrough)

---

## Overview

The RoofTop Food Delivery System is a Spring Boot REST API that efficiently assigns delivery drivers to customer orders based on availability and timing constraints.

### Key Features
- Real-time driver assignment algorithm
- RESTful API with 6 endpoints
- H2 in-memory database (MySQL compatible)
- Complete audit trail of all assignments
- Input validation and error handling

---

## System Architecture

### Architecture Layers

```
┌─────────────────────────────────────────┐
│         REST API Controller             │
│  (DeliveryController.java)              │
│  - Process orders                       │
│  - Get drivers/customers/assignments    │
│  - Reset system                         │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         Service Layer                   │
│  (DeliveryAssignmentService.java)       │
│  - Assignment algorithm                 │
│  - Business logic                       │
│  - Driver initialization                │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         Repository Layer                │
│  (Spring Data JPA)                      │
│  - DriverRepository                     │
│  - CustomerRepository                   │
│  - DeliveryAssignmentRepository         │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         Database (H2/MySQL)             │
│  - drivers table                        │
│  - customers table                      │
│  - delivery_assignments table           │
└─────────────────────────────────────────┘
```

### Component Description

#### 1. **Controller Layer**
- Handles HTTP requests and responses
- Input validation using `@Valid` annotations
- Returns JSON responses

#### 2. **Service Layer**
- Core assignment algorithm
- Driver availability management
- Business rule enforcement

#### 3. **Repository Layer**
- Database operations using Spring Data JPA
- Custom queries for driver ordering
- CRUD operations

#### 4. **Database Layer**
- H2 in-memory database (default)
- MySQL support (optional)
- Persistent storage of all transactions

---

## Algorithm Explanation

### Core Assignment Logic

```java
private String assignDriver(List<Driver> drivers, int orderTime, int travelTime) {
    // 1. Find first available driver
    for (Driver driver : drivers) {
        if (driver.getAvailableAt() <= orderTime) {
            // 2. Assign driver
            driver.setStatus("Busy");
            driver.setAvailableAt(orderTime + travelTime);
            driverRepository.save(driver);
            return driver.getDriverId();
        }
    }
    // 3. No driver available
    return "No Food :-(";
}
```

### Algorithm Flow

```
START
  ↓
Initialize M drivers (D1, D2, ..., DM)
All drivers: availableAt = 0
  ↓
FOR each customer order:
  ↓
  Check D1 → availableAt <= orderTime?
    ├── YES → Assign D1
    │         Update: availableAt = orderTime + travelTime
    │         NEXT order
    │
    └── NO → Check D2 → availableAt <= orderTime?
              ├── YES → Assign D2
              │         Update: availableAt = orderTime + travelTime
              │         NEXT order
              │
              └── NO → Check D3... → All busy?
                        └── YES → "No Food :-("
                                  NEXT order
  ↓
END
```

### Time Complexity
- **Per order**: O(M) where M = number of drivers
- **Total**: O(N × M) where N = number of orders
- **Space**: O(N + M) for storing drivers and customers

---

## API Documentation

### Base URL
```
http://localhost:8080/api/delivery
```

### Endpoints

#### 1. Process Batch Orders

**POST** `/process`

Process multiple customer orders and assign drivers.

**Request:**
```json
{
  "numberOfCustomers": 6,
  "numberOfDrivers": 2,
  "orders": [
    {"orderTime": 1, "travelTime": 10},
    {"orderTime": 4, "travelTime": 20},
    {"orderTime": 15, "travelTime": 5},
    {"orderTime": 22, "travelTime": 20},
    {"orderTime": 24, "travelTime": 10},
    {"orderTime": 25, "travelTime": 10}
  ]
}
```

**Response:**
```json
{
  "status": "success",
  "totalCustomers": 6,
  "totalDrivers": 2,
  "assignments": [
    {"customerId": "C1", "assignedDriver": "D1", "message": "C1 - D1"},
    {"customerId": "C2", "assignedDriver": "D2", "message": "C2 - D2"},
    {"customerId": "C3", "assignedDriver": "D1", "message": "C3 - D1"},
    {"customerId": "C4", "assignedDriver": "D1", "message": "C4 - D1"},
    {"customerId": "C5", "assignedDriver": "D2", "message": "C5 - D2"},
    {"customerId": "C6", "assignedDriver": "No Food :-(", "message": "C6 - No Food :-("}
  ]
}
```

---

#### 2. Get All Drivers

**GET** `/drivers`

Retrieve all drivers with their current status.

**Response:**
```json
[
  {
    "id": 1,
    "driverId": "D1",
    "status": "Busy",
    "availableAt": 42,
    "currentOrderId": null,
    "createdAt": "2026-02-05T17:45:00"
  },
  {
    "id": 2,
    "driverId": "D2",
    "status": "Busy",
    "availableAt": 34,
    "currentOrderId": null,
    "createdAt": "2026-02-05T17:45:00"
  }
]
```

---

#### 3. Get All Customers

**GET** `/customers`

Retrieve all customers with their assignments.

**Response:**
```json
[
  {
    "id": 1,
    "customerId": "C1",
    "orderTime": 1,
    "travelTime": 10,
    "assignedDriver": "D1",
    "status": "ASSIGNED",
    "createdAt": "2026-02-05T17:45:01"
  },
  {
    "id": 6,
    "customerId": "C6",
    "orderTime": 25,
    "travelTime": 10,
    "assignedDriver": "No Food :-(",
    "status": "REJECTED",
    "createdAt": "2026-02-05T17:45:06"
  }
]
```

---

#### 4. Get All Assignments

**GET** `/assignments`

Retrieve complete audit trail of all assignments.

**Response:**
```json
[
  {
    "id": 1,
    "customerId": "C1",
    "driverId": "D1",
    "orderTime": 1,
    "assignmentTime": 1,
    "completionTime": 11,
    "assignmentResult": "D1",
    "createdAt": "2026-02-05T17:45:01"
  }
]
```

---

#### 5. Reset System

**POST** `/reset`

Clear all data and reset the system.

**Response:**
```json
{
  "status": "success",
  "message": "System reset successfully"
}
```

---

#### 6. Health Check

**GET** `/health`

Check if the service is running.

**Response:**
```json
{
  "status": "UP",
  "service": "RoofTop Food Delivery System"
}
```

---

## Database Schema

### Tables

#### 1. drivers
| Column         | Type         | Description                      |
|----------------|--------------|----------------------------------|
| id             | BIGINT       | Primary key (auto-increment)     |
| driver_id      | VARCHAR(10)  | Unique driver ID (D1, D2, ...)  |
| status         | VARCHAR(20)  | Available/Busy                   |
| available_at   | INT          | Time when driver becomes free    |
| current_order_id | VARCHAR(10) | Current order being delivered   |
| created_at     | TIMESTAMP    | Record creation time             |

**Indexes:**
- `idx_driver_id` on `driver_id`
- `idx_available_at` on `available_at`

---

#### 2. customers
| Column          | Type         | Description                      |
|-----------------|--------------|----------------------------------|
| id              | BIGINT       | Primary key (auto-increment)     |
| customer_id     | VARCHAR(10)  | Customer ID (C1, C2, ...)       |
| order_time      | INT          | Order placement time             |
| travel_time     | INT          | Travel duration                  |
| assigned_driver | VARCHAR(50)  | Assigned driver or "No Food"    |
| status          | VARCHAR(20)  | ASSIGNED/REJECTED                |
| created_at      | TIMESTAMP    | Record creation time             |

**Indexes:**
- `idx_customer_id` on `customer_id`
- `idx_order_time` on `order_time`

---

#### 3. delivery_assignments
| Column           | Type         | Description                      |
|------------------|--------------|----------------------------------|
| id               | BIGINT       | Primary key (auto-increment)     |
| customer_id      | VARCHAR(10)  | Customer reference               |
| driver_id        | VARCHAR(10)  | Driver reference                 |
| order_time       | INT          | Order placement time             |
| assignment_time  | INT          | When assignment was made         |
| completion_time  | INT          | Expected completion time         |
| assignment_result| VARCHAR(50)  | Result of assignment             |
| created_at       | TIMESTAMP    | Record creation time             |

**Indexes:**
- `idx_customer_id` on `customer_id`
- `idx_driver_id` on `driver_id`
- `idx_order_time` on `order_time`

---

## Screenshots

### 1. Health Check API

<img src="screenshots/healthapi.png" alt="Health Check API" width="900"/>

*Health check endpoint - verifies service is running properly*

---

### 2. Process Orders API (POST)

<img src="screenshots/apideliveryprocesspost.png" alt="Process Orders API" width="900"/>

*Main endpoint - processes batch orders and assigns drivers to customers*

---

### 3. Get Drivers API (GET)

<img src="screenshots/apideliverydriversget.png" alt="Get Drivers API" width="900"/>

*Retrieves all drivers with their current status and availability times*

---

### 4. Get Customers API (GET)

<img src="screenshots/apideliverycustomersget.png" alt="Get Customers API" width="900"/>

*Retrieves all customers with their order details and assigned drivers*

---

### 5. Get Assignments API (GET)

<img src="screenshots/apideliveryassignmentsget.png" alt="Get Assignments API" width="900"/>

*Complete audit trail of all delivery assignments with timestamps*

---

### 6. Reset System API (POST)

<img src="screenshots/apideliveryresetpost.png" alt="Reset System API" width="900"/>

*Clears all data and resets the system to initial state*

---

## Example Walkthrough

### Scenario: 6 Customers, 2 Drivers

**Input:**
```
Customer 1: Order at time 1, travel time 10
Customer 2: Order at time 4, travel time 20
Customer 3: Order at time 15, travel time 5
Customer 4: Order at time 22, travel time 20
Customer 5: Order at time 24, travel time 10
Customer 6: Order at time 25, travel time 10
```

---

### Assignment Process

#### Customer 1 (Order: 1, Travel: 10)
```
Time: 1
D1 available at: 0 (✓ available)
D2 available at: 0

→ Assign D1
→ D1 now available at: 1 + 10 = 11
```
**Result:** C1 - D1

---

#### Customer 2 (Order: 4, Travel: 20)
```
Time: 4
D1 available at: 11 (✗ busy)
D2 available at: 0 (✓ available)

→ Assign D2
→ D2 now available at: 4 + 20 = 24
```
**Result:** C2 - D2

---

#### Customer 3 (Order: 15, Travel: 5)
```
Time: 15
D1 available at: 11 (✓ available)
D2 available at: 24 (✗ busy)

→ Assign D1
→ D1 now available at: 15 + 5 = 20
```
**Result:** C3 - D1

---

#### Customer 4 (Order: 22, Travel: 20)
```
Time: 22
D1 available at: 20 (✓ available)
D2 available at: 24 (✗ busy)

→ Assign D1
→ D1 now available at: 22 + 20 = 42
```
**Result:** C4 - D1

---

#### Customer 5 (Order: 24, Travel: 10)
```
Time: 24
D1 available at: 42 (✗ busy)
D2 available at: 24 (✓ available)

→ Assign D2
→ D2 now available at: 24 + 10 = 34
```
**Result:** C5 - D2

---

#### Customer 6 (Order: 25, Travel: 10)
```
Time: 25
D1 available at: 42 (✗ busy - still 17 time units away)
D2 available at: 34 (✗ busy - still 9 time units away)

→ No driver available
```
**Result:** C6 - No Food :-(

---

### Summary Table

| Customer | Order Time | Travel Time | D1 Available | D2 Available | Assigned |
|----------|-----------|-------------|--------------|--------------|----------|
| C1       | 1         | 10          | 0            | 0            | D1       |
| C2       | 4         | 20          | 11           | 0            | D2       |
| C3       | 15        | 5           | 11           | 24           | D1       |
| C4       | 22        | 20          | 20           | 24           | D1       |
| C5       | 24        | 10          | 42           | 24           | D2       |
| C6       | 25        | 10          | 42           | 34           | No Food  |

---

## Testing Guide

### Using cURL

```bash
# 1. Start the application
mvn spring-boot:run

# 2. Health check
curl http://localhost:8080/api/delivery/health

# 3. Process orders
curl -X POST http://localhost:8080/api/delivery/process \
  -H "Content-Type: application/json" \
  -d '{
    "numberOfCustomers": 6,
    "numberOfDrivers": 2,
    "orders": [
      {"orderTime": 1, "travelTime": 10},
      {"orderTime": 4, "travelTime": 20},
      {"orderTime": 15, "travelTime": 5},
      {"orderTime": 22, "travelTime": 20},
      {"orderTime": 24, "travelTime": 10},
      {"orderTime": 25, "travelTime": 10}
    ]
  }'

# 4. View drivers
curl http://localhost:8080/api/delivery/drivers

# 5. View customers
curl http://localhost:8080/api/delivery/customers

# 6. View assignments
curl http://localhost:8080/api/delivery/assignments

# 7. Reset system
curl -X POST http://localhost:8080/api/delivery/reset
```

---

### Using Postman

1. Import the base URL: `http://localhost:8080`
2. Create requests for each endpoint
3. Set Content-Type: `application/json` for POST requests
4. Use the JSON examples provided above

---

## Troubleshooting

### Common Issues

#### 1. Port 8080 already in use
```bash
# Find and kill process using port 8080
lsof -ti:8080 | xargs kill -9
```

#### 2. Maven build fails
```bash
# Clean and rebuild
mvn clean install -U
```

#### 3. Java version mismatch
```bash
# Check Java version
java -version

# Should be Java 17
```

---

## Performance Considerations

### Optimization Strategies

1. **Database Indexing**: Indexes on `driver_id`, `available_at`, and `order_time` for faster queries
2. **Transaction Management**: `@Transactional` ensures data consistency
3. **In-Memory Database**: H2 provides fast read/write operations for testing
4. **Ordered Queries**: `findAllByOrderByDriverIdAsc()` ensures correct driver priority

### Scalability

- **Current**: Handles hundreds of orders efficiently
- **Production**: Consider MySQL for persistent storage
- **Enhancement**: Add caching layer (Redis) for high-volume scenarios

---

## Future Enhancements

1. **Real-time Updates**: WebSocket support for live tracking
2. **Driver Location**: GPS integration for dynamic assignment
3. **Priority Orders**: VIP customer handling
4. **Analytics Dashboard**: Reporting and metrics
5. **Mobile App**: Driver and customer mobile applications
6. **Notification System**: SMS/Email notifications
7. **Payment Integration**: Payment gateway integration
