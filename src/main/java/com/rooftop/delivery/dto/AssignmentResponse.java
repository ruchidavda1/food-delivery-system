package com.rooftop.delivery.dto;

public class AssignmentResponse {
    
    private String customerId;
    private String assignedDriver;
    private String message;
    
    public AssignmentResponse() {
    }
    
    public AssignmentResponse(String customerId, String assignedDriver) {
        this.customerId = customerId;
        this.assignedDriver = assignedDriver;
        this.message = customerId + " - " + assignedDriver;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public String getAssignedDriver() {
        return assignedDriver;
    }
    
    public void setAssignedDriver(String assignedDriver) {
        this.assignedDriver = assignedDriver;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
