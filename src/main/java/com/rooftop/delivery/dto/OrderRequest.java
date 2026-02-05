package com.rooftop.delivery.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class OrderRequest {
    
    @NotNull(message = "Order time is required")
    @Positive(message = "Order time must be positive")
    private Integer orderTime;
    
    @NotNull(message = "Travel time is required")
    @Positive(message = "Travel time must be positive")
    private Integer travelTime;
    
    public OrderRequest() {
    }
    
    public OrderRequest(Integer orderTime, Integer travelTime) {
        this.orderTime = orderTime;
        this.travelTime = travelTime;
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
}
