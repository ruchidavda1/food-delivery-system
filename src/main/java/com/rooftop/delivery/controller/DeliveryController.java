package com.rooftop.delivery.controller;

import com.rooftop.delivery.dto.AssignmentResponse;
import com.rooftop.delivery.dto.BatchOrderRequest;
import com.rooftop.delivery.model.Customer;
import com.rooftop.delivery.model.DeliveryAssignment;
import com.rooftop.delivery.model.Driver;
import com.rooftop.delivery.service.DeliveryAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {
    
    @Autowired
    private DeliveryAssignmentService deliveryService;
    
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processBatchOrders(
            @Valid @RequestBody BatchOrderRequest request) {
        
        List<AssignmentResponse> assignments = deliveryService.processBatchOrders(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("totalCustomers", request.getNumberOfCustomers());
        response.put("totalDrivers", request.getNumberOfDrivers());
        response.put("assignments", assignments);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/drivers")
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return ResponseEntity.ok(deliveryService.getAllDrivers());
    }
    
    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(deliveryService.getAllCustomers());
    }
    
    @GetMapping("/assignments")
    public ResponseEntity<List<DeliveryAssignment>> getAllAssignments() {
        return ResponseEntity.ok(deliveryService.getAllAssignments());
    }
    
    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetSystem() {
        deliveryService.resetSystem();
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "System reset successfully");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "RoofTop Food Delivery System");
        
        return ResponseEntity.ok(response);
    }
}
