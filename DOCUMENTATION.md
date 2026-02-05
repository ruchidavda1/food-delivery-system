# Technical Documentation

## Architecture

### Layers

```
Controller (REST API)
    ↓
Service (Business Logic)
    ↓
Repository (Data Access)
    ↓
Database (H2/MySQL)
```

### Components

**DeliveryController**: Handles HTTP requests, validates input, returns JSON  
**DeliveryAssignmentService**: Core assignment algorithm, driver management  
**Repositories**: Spring Data JPA interfaces for database operations  
**Models**: Driver, Customer, DeliveryAssignment entities

## Algorithm

### Main Flow

The `processBatchOrders()` method handles the core logic:

1. Initialize M drivers (D1, D2, etc.) with `availableAt = 0`
2. Load drivers from DB (sorted by ID to maintain priority)
3. Add all drivers to a PriorityQueue (min-heap)
4. For each order:
   - Call `assignDriverOptimized()` to get an available driver
   - Save customer record with assignment result
   - Log to audit trail
5. Return assignment responses

### Driver Assignment

The key optimization is using a **PriorityQueue** instead of iterating through all drivers.

The queue sorts drivers by:
1. `availableAt` time (earliest first)
2. `driverId` (D1 before D2 if both free at same time)

For each order:
- Peek at the top of heap (next available driver)
- If `driver.availableAt <= orderTime`: assign them
- Update driver's `availableAt = orderTime + travelTime`
- Re-insert into heap
- If no driver is free: return "No Food :-("

This approach means we don't check every driver for every order. The heap maintains the next available driver at the top, so we only need O(log M) operations per assignment.

**Performance:** O(N log M) vs O(N×M) for naive iteration  
Example: 1000 orders × 100 drivers = ~6,600 ops vs 100,000 ops

### Audit Trail

Each assignment (success or failure) gets logged to `delivery_assignments` table with order time, driver ID, and completion time. Useful for debugging and analytics.

## Data Models

### DTOs (Data Transfer Objects)

**OrderRequest**: Contains orderTime and travelTime with validation annotations  
**BatchOrderRequest**: Contains numberOfCustomers, numberOfDrivers, and list of OrderRequest objects  
**AssignmentResponse**: Contains customerId, assignedDriver, and formatted message for output

### Entities

**Driver**: Stores driver_id, status (Available/Busy), availableAt time, and timestamps  
**Customer**: Stores customer_id, order details, assigned_driver, status (ASSIGNED/REJECTED)  
**DeliveryAssignment**: Audit record with customer_id, driver_id, times, and assignment result

### Repositories

Spring Data JPA interfaces that extend JpaRepository:

**DriverRepository**: Has custom query `findAllByOrderByDriverIdAsc()` to maintain driver priority  
**CustomerRepository**: Standard CRUD operations  
**DeliveryAssignmentRepository**: Standard CRUD operations

## API Reference

### POST /api/delivery/process

Process batch orders and assign drivers.

**Request:**
```json
{
  "numberOfCustomers": 6,
  "numberOfDrivers": 2,
  "orders": [
    {"orderTime": 1, "travelTime": 10},
    {"orderTime": 4, "travelTime": 20}
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
    {"customerId": "C2", "assignedDriver": "D2", "message": "C2 - D2"}
  ]
}
```

### GET /api/delivery/drivers

Returns all drivers with status and availability.

**Response:**
```json
[
  {
    "id": 1,
    "driverId": "D1",
    "status": "Busy",
    "availableAt": 42,
    "createdAt": "2026-02-05T17:45:00"
  }
]
```

### GET /api/delivery/customers

Returns all customers with assignments.

**Response:**
```json
[
  {
    "id": 1,
    "customerId": "C1",
    "orderTime": 1,
    "travelTime": 10,
    "assignedDriver": "D1",
    "status": "ASSIGNED"
  }
]
```

### GET /api/delivery/assignments

Returns complete assignment audit trail.

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
    "assignmentResult": "D1"
  }
]
```

### POST /api/delivery/reset

Clears all data from database.

**Response:**
```json
{
  "status": "success",
  "message": "System reset successfully"
}
```

### GET /api/delivery/health

Health check endpoint.

**Response:**
```json
{
  "status": "UP",
  "service": "RoofTop Food Delivery System"
}
```

## Database Schema

### drivers
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| driver_id | VARCHAR(10) | D1, D2, D3... |
| status | VARCHAR(20) | Available/Busy |
| available_at | INT | When driver is free |
| current_order_id | VARCHAR(10) | Current delivery |
| created_at | TIMESTAMP | Creation time |

Indexes: `driver_id`, `available_at`

### customers
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| customer_id | VARCHAR(10) | C1, C2, C3... |
| order_time | INT | Order placement time |
| travel_time | INT | Delivery duration |
| assigned_driver | VARCHAR(50) | Driver or "No Food" |
| status | VARCHAR(20) | ASSIGNED/REJECTED |
| created_at | TIMESTAMP | Creation time |

Indexes: `customer_id`, `order_time`

### delivery_assignments
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| customer_id | VARCHAR(10) | Customer reference |
| driver_id | VARCHAR(10) | Driver reference |
| order_time | INT | Order time |
| assignment_time | INT | Assignment time |
| completion_time | INT | Expected completion |
| assignment_result | VARCHAR(50) | Assignment result |
| created_at | TIMESTAMP | Creation time |

Indexes: `customer_id`, `driver_id`, `order_time`

## Step-by-Step Example

**Setup**: 6 customers, 2 drivers

### Customer 1: Order at 1, Travel 10
- D1 available at 0 (Yes)
- Assign D1
- D1 now free at: 1 + 10 = 11
- **Result: C1 - D1**

### Customer 2: Order at 4, Travel 20
- D1 available at 11 (busy)
- D2 available at 0 (Yes)
- Assign D2
- D2 now free at: 4 + 20 = 24
- **Result: C2 - D2**

### Customer 3: Order at 15, Travel 5
- D1 available at 11 (Yes)
- Assign D1
- D1 now free at: 15 + 5 = 20
- **Result: C3 - D1**

### Customer 4: Order at 22, Travel 20
- D1 available at 20 (Yes)
- Assign D1
- D1 now free at: 22 + 20 = 42
- **Result: C4 - D1**

### Customer 5: Order at 24, Travel 10
- D1 available at 42 (busy)
- D2 available at 24 (Yes)
- Assign D2
- D2 now free at: 24 + 10 = 34
- **Result: C5 - D2**

### Customer 6: Order at 25, Travel 10
- D1 available at 42 (busy)
- D2 available at 34 (busy)
- No driver available
- **Result: C6 - No Food :-(**

## Configuration

### H2 Database (Default)

In `application.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:rooftop_delivery
spring.datasource.driver-class-name=org.h2.Driver
spring.h2.console.enabled=true
```

### MySQL (Optional)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/rooftop_delivery
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
```

Run `schema-mysql.sql` to create tables.

## Testing

### Using cURL

```bash
# Health check
curl http://localhost:8080/api/delivery/health

# Process orders
curl -X POST http://localhost:8080/api/delivery/process \
  -H "Content-Type: application/json" \
  -d @- <<EOF
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
EOF

# View drivers
curl http://localhost:8080/api/delivery/drivers

# View customers
curl http://localhost:8080/api/delivery/customers

# View assignments
curl http://localhost:8080/api/delivery/assignments

# Reset
curl -X POST http://localhost:8080/api/delivery/reset
```

### Using Postman

1. Set base URL: `http://localhost:8080`
2. Import endpoints from API Reference section
3. Test each endpoint with sample data

## Screenshots

### 1. Health Check API
<img src="screenshots/healthapi.png" alt="Health Check" width="900"/>

Verifies the service is running.

### 2. Process Orders API
<img src="screenshots/apideliveryprocesspost.png" alt="Process Orders" width="900"/>

Main endpoint that processes batch orders and assigns drivers.

### 3. Get Drivers API
<img src="screenshots/apideliverydriversget.png" alt="Get Drivers" width="900"/>

Shows all drivers with their current status and availability times.

### 4. Get Customers API
<img src="screenshots/apideliverycustomersget.png" alt="Get Customers" width="900"/>

Lists all customers with their order details and assigned drivers.

### 5. Get Assignments API
<img src="screenshots/apideliveryassignmentsget.png" alt="Get Assignments" width="900"/>

Complete audit trail of all assignments with timestamps.

### 6. Reset System API
<img src="screenshots/apideliveryresetpost.png" alt="Reset System" width="900"/>

Clears all data and resets the system.

