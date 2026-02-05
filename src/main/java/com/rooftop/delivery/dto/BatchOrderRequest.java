package com.rooftop.delivery.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

public class BatchOrderRequest {
    
    @NotNull(message = "Number of customers is required")
    @Positive(message = "Number of customers must be positive")
    private Integer numberOfCustomers;
    
    @NotNull(message = "Number of drivers is required")
    @Positive(message = "Number of drivers must be positive")
    private Integer numberOfDrivers;
    
    @NotEmpty(message = "Orders list cannot be empty")
    @Valid
    private List<OrderRequest> orders;
    
    public BatchOrderRequest() {
    }
    
    public Integer getNumberOfCustomers() {
        return numberOfCustomers;
    }
    
    public void setNumberOfCustomers(Integer numberOfCustomers) {
        this.numberOfCustomers = numberOfCustomers;
    }
    
    public Integer getNumberOfDrivers() {
        return numberOfDrivers;
    }
    
    public void setNumberOfDrivers(Integer numberOfDrivers) {
        this.numberOfDrivers = numberOfDrivers;
    }
    
    public List<OrderRequest> getOrders() {
        return orders;
    }
    
    public void setOrders(List<OrderRequest> orders) {
        this.orders = orders;
    }
}
