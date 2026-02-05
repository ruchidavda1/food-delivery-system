package com.rooftop.delivery.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "drivers")
public class Driver {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String driverId;
    
    @Column(nullable = false)
    private String status;
    
    @Column
    private Integer availableAt;
    
    @Column
    private String currentOrderId;
    
    @Column
    private LocalDateTime createdAt;
    
    public Driver() {
        this.status = "Available";
        this.availableAt = 0;
        this.createdAt = LocalDateTime.now();
    }
    
    public Driver(String driverId) {
        this.driverId = driverId;
        this.status = "Available";
        this.availableAt = 0;
        this.createdAt = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDriverId() {
        return driverId;
    }
    
    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getAvailableAt() {
        return availableAt;
    }
    
    public void setAvailableAt(Integer availableAt) {
        this.availableAt = availableAt;
    }
    
    public String getCurrentOrderId() {
        return currentOrderId;
    }
    
    public void setCurrentOrderId(String currentOrderId) {
        this.currentOrderId = currentOrderId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
