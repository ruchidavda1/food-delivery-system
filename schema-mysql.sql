-- MySQL Schema for RoofTop Food Delivery System
-- Optional: Use this if you want to run with MySQL instead of H2

-- Create database
CREATE DATABASE IF NOT EXISTS rooftop_delivery;
USE rooftop_delivery;

-- Drop tables if they exist
DROP TABLE IF EXISTS delivery_assignments;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS drivers;

-- Drivers table
CREATE TABLE drivers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    driver_id VARCHAR(10) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'Available',
    available_at INT NOT NULL DEFAULT 0,
    current_order_id VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_driver_id (driver_id),
    INDEX idx_available_at (available_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Customers table
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(10) NOT NULL,
    order_time INT NOT NULL,
    travel_time INT NOT NULL,
    assigned_driver VARCHAR(50),
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_customer_id (customer_id),
    INDEX idx_order_time (order_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Delivery assignments table
CREATE TABLE delivery_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(10) NOT NULL,
    driver_id VARCHAR(10),
    order_time INT NOT NULL,
    assignment_time INT,
    completion_time INT,
    assignment_result VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_customer_id (customer_id),
    INDEX idx_driver_id (driver_id),
    INDEX idx_order_time (order_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample drivers (optional)
-- INSERT INTO drivers (driver_id, status, available_at) VALUES
-- ('D1', 'Available', 0),
-- ('D2', 'Available', 0);

-- Success message
SELECT 'Database schema created successfully!' as message;
