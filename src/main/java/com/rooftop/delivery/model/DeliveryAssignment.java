package com.rooftop.delivery.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_assignments")
public class DeliveryAssignment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String customerId;
    
    @Column
    private String driverId;
    
    @Column(nullable = false)
    private Integer orderTime;
    
    @Column
    private Integer assignmentTime;
    
    @Column
    private Integer completionTime;
    
    @Column(nullable = false)
    private String assignmentResult;
    
    @Column
    private LocalDateTime createdAt;
    
    public DeliveryAssignment() {
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
    
    public String getDriverId() {
        return driverId;
    }
    
    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
    
    public Integer getOrderTime() {
        return orderTime;
    }
    
    public void setOrderTime(Integer orderTime) {
        this.orderTime = orderTime;
    }
    
    public Integer getAssignmentTime() {
        return assignmentTime;
    }
    
    public void setAssignmentTime(Integer assignmentTime) {
        this.assignmentTime = assignmentTime;
    }
    
    public Integer getCompletionTime() {
        return completionTime;
    }
    
    public void setCompletionTime(Integer completionTime) {
        this.completionTime = completionTime;
    }
    
    public String getAssignmentResult() {
        return assignmentResult;
    }
    
    public void setAssignmentResult(String assignmentResult) {
        this.assignmentResult = assignmentResult;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
