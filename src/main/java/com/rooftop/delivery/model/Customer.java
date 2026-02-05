package com.rooftop.delivery.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String customerId;
    
    @Column(nullable = false)
    private Integer orderTime;
    
    @Column(nullable = false)
    private Integer travelTime;
    
    @Column
    private String assignedDriver;
    
    @Column
    private String status;
    
    @Column
    private LocalDateTime createdAt;
    
    public Customer() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Customer(String customerId, Integer orderTime, Integer travelTime) {
        this.customerId = customerId;
        this.orderTime = orderTime;
        this.travelTime = travelTime;
        this.createdAt = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public Integer getOrderTime() {
        return orderTime;
    }
    
    public void setOrderTime(Integer orderTime) {
        this.orderTime = orderTime;
    }
    
    public Integer getTravelTime() {
        return travelTime;
    }
    
    public void setTravelTime(Integer travelTime) {
        this.travelTime = travelTime;
    }
    
    public String getAssignedDriver() {
        return assignedDriver;
    }
    
    public void setAssignedDriver(String assignedDriver) {
        this.assignedDriver = assignedDriver;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
