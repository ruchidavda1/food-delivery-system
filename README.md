# RoofTop Food Delivery System

A cloud-based food delivery driver assignment system built for RoofTop Restaurant in New York. The system efficiently assigns delivery drivers to customer orders based on driver availability and order timing.

## Problem Statement

The system manages food delivery by assigning M drivers to N customer orders. Each customer order has:
- **O**: Order placement time
- **T**: Travel time from restaurant to customer

### Assignment Rules
1. Assign the lowest index driver first (D1, D2, ..., DM)
2. Drivers can handle only one order at a time
3. Driver status becomes "Busy" during delivery
4. If all drivers are busy, display "No Food :-("

## Technology Stack

- **Java**: 17
- **Spring Boot**: 2.7.14
- **Spring Data JPA**: Database operations
- **H2 Database**: In-memory database H2 used as mentioned. Hence used H2 instead of MySQL.
- **Maven**: Build tool
- **RESTful Web Services**: API endpoints

**Note:** The project is configured with H2 in-memory database for easy testing. MySQL configuration is also available (commented out in application.properties).

## Project Structure

```
food-delivery-system/
├── src/
│   └── main/
│       ├── java/com/rooftop/delivery/
│       │   ├── FoodDeliveryApplication.java
│       │   ├── model/
│       │   │   ├── Customer.java
│       │   │   ├── Driver.java
│       │   │   └── DeliveryAssignment.java
│       │   ├── repository/
│       │   │   ├── CustomerRepository.java
│       │   │   ├── DriverRepository.java
│       │   │   └── DeliveryAssignmentRepository.java
│       │   ├── service/
│       │   │   └── DeliveryAssignmentService.java
│       │   ├── controller/
│       │   │   └── DeliveryController.java
│       │   └── dto/
│       │       ├── OrderRequest.java
│       │       ├── BatchOrderRequest.java
│       │       └── AssignmentResponse.java
│       └── resources/
│           └── application.properties
├── pom.xml
├── input.txt
├── schema-mysql.sql
├── .gitignore
└── README.md
```

## Prerequisites

1. **Java JDK 17**
2. **Maven 3.6+**
3. **IDE**: VS Code, Eclipse, IntelliJ IDEA etc

The project uses H2 in-memory database. As mentioned to use H2, hence used H2 instead of MySQL.

## Quick Start (3 Steps)

### Step 1: Build the Project

```bash
mvn clean install
```

### Step 2: Run the Application

```bash
mvn spring-boot:run
```

Wait for the message:
```
========================================
RoofTop Food Delivery System Started
========================================
```

### Step 3: Test It

Open a new terminal and run:

```bash
# Health check
curl http://localhost:8080/api/delivery/health

# Test the main functionality
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
```

**Expected Output:** C1-D1, C2-D2, C3-D1, C4-D1, C5-D2, C6-No Food :-(

### H2 Database Console (Optional)

View the database in your browser:
- **URL:** http://localhost:8080/h2-console
- **JDBC URL:** `jdbc:h2:mem:rooftop_delivery`
- **Username:** `sa`
- **Password:** (leave empty)

---

## Using MySQL (Optional Alternative)

If you prefer to use MySQL instead of H2:

### Step 1: Install MySQL

Download and install MySQL 8.0+ from [mysql.com](https://dev.mysql.com/downloads/)

### Step 2: Create Database

Run the provided SQL script:

```bash
mysql -u root -p < schema-mysql.sql
```

Or manually create the database:

```sql
CREATE DATABASE rooftop_delivery;
```

### Step 3: Update Configuration

Edit `src/main/resources/application.properties`:

**Comment out H2 configuration:**
```properties
# spring.datasource.url=jdbc:h2:mem:rooftop_delivery
# spring.datasource.driver-class-name=org.h2.Driver
# spring.h2.console.enabled=true
```

**Uncomment MySQL configuration:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/rooftop_delivery?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
```

### Step 4: Restart Application

```bash
mvn spring-boot:run
```

Data will now persist in MySQL database!

---

## Building the Project

### Using Maven

```bash
# Clean and build
mvn clean install

# Run tests
mvn test

# Package as JAR
mvn package
```

### Using IDE

1. Import as Maven project
2. Build project using IDE's build tools
3. Run `FoodDeliveryApplication.java`

## Running the Application

### Method 1: Spring Boot Application (Web Service)

```bash
# Run the application
mvn spring-boot:run

# Or run the JAR
java -jar target/food-delivery-assignment-1.0.0.jar
```

The REST API available at `http://localhost:8080`

### Method 2: Command Line with Input File

```bash
java -jar target/food-delivery-assignment-1.0.0.jar --process-file input.txt
```

## API Endpoints

### 1. Process Batch Orders

**POST** `/api/delivery/process`

Request body:
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

Response:
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

### 2. Get All Drivers

**GET** `/api/delivery/drivers`

### 3. Get All Customers

**GET** `/api/delivery/customers`

### 4. Get All Assignments

**GET** `/api/delivery/assignments`

### 5. Reset System

**POST** `/api/delivery/reset`

### 6. Health Check

**GET** `/api/delivery/health`

## Testing with cURL

```bash
# Health check
curl http://localhost:8080/api/delivery/health

# Process orders
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

# Get all drivers
curl http://localhost:8080/api/delivery/drivers

# Reset system
curl -X POST http://localhost:8080/api/delivery/reset
```

## Testing with Postman

1. Import the API endpoints
2. Set base URL: `http://localhost:8080`
3. Use the endpoints listed above
4. Check responses in Postman console

## Input File Format

Create a text file (e.g., `input.txt`):

```
6,2
1,10
4,20
15,5
22,20
24,10
25,10
```

- Line 1: `N,M` (number of customers, number of drivers)
- Lines 2-N+1: `O,T` (order time, travel time)

## Expected Output

```
C1 - D1
C2 - D2
C3 - D1
C4 - D1
C5 - D2
C6 - No Food :-(
```

## Business Logic Explanation

### Algorithm Flow

1. **Initialize Drivers**: Create M drivers with status "Available" and `availableAt = 0`
2. **Process Orders Sequentially**: For each customer order:
   - Find the first driver (lowest index) where `availableAt <= orderTime`
   - If found:
     - Assign driver to customer
     - Update driver status to "Busy"
     - Set `availableAt = orderTime + travelTime`
   - If not found:
     - Return "No Food :-("

### Example Walkthrough

Given: 6 customers, 2 drivers

| Customer | Order Time | Travel Time | D1 Available | D2 Available | Assigned |
|----------|-----------|-------------|--------------|--------------|----------|
| C1       | 1         | 10          | 0            | 0            | D1       |
| C2       | 4         | 20          | 11           | 0            | D2       |
| C3       | 15        | 5           | 11           | 24           | D1       |
| C4       | 22        | 20          | 20           | 24           | D1       |
| C5       | 24        | 10          | 42           | 24           | D2       |
| C6       | 25        | 10          | 42           | 34           | No Food  |

## Database Schema

### Drivers Table
- `id`: Primary key
- `driver_id`: Unique driver identifier (D1, D2, ...)
- `status`: Current status (Available/Busy)
- `available_at`: Time when driver becomes available
- `current_order_id`: Current order being delivered
- `created_at`: Timestamp

### Customers Table
- `id`: Primary key
- `customer_id`: Customer identifier (C1, C2, ...)
- `order_time`: Order placement time
- `travel_time`: Travel duration
- `assigned_driver`: Assigned driver ID or "No Food :-("
- `status`: Order status (ASSIGNED/REJECTED)
- `created_at`: Timestamp

### Delivery Assignments Table
- `id`: Primary key
- `customer_id`: Customer reference
- `driver_id`: Driver reference
- `order_time`: Order placement time
- `assignment_time`: When assignment was made
- `completion_time`: Expected completion time
- `assignment_result`: Result of assignment
- `created_at`: Timestamp

---

## Project Files

### Source Code
- `src/` - All Java source code
- `pom.xml` - Maven configuration
- `README.md` - This documentation

### Database Scripts
- `schema-mysql.sql` - MySQL database schema (optional)

### Sample Data
- `input.txt` - Sample test input

### Configuration
- `application.properties` - Spring Boot configuration (H2 by default, MySQL optional)

---

## Features

- Driver assignment algorithm (lowest index priority)
- RESTful web services (6 API endpoints)
- H2 in-memory database (default)
- MySQL support (optional)
- Spring Data JPA with Repository pattern
- Complete data persistence and audit trail
- Input validation
- Clean architecture (Controller-Service-Repository)


## Technology Highlights

- **Spring Boot 2.7.14** - Enterprise Java framework
- **JPA/Hibernate** - Object-relational mapping
- **H2 Database** - In-memory database (default)
- **MySQL 8.0** - Production database (optional)
- **REST API** - JSON web services
- **Maven** - Build automation
- **Java 17** - Modern LTS Java version
# food-delivery-system
