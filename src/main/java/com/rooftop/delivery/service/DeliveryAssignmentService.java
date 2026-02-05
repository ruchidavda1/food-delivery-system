package com.rooftop.delivery.service;

import com.rooftop.delivery.dto.AssignmentResponse;
import com.rooftop.delivery.dto.BatchOrderRequest;
import com.rooftop.delivery.dto.OrderRequest;
import com.rooftop.delivery.model.Customer;
import com.rooftop.delivery.model.DeliveryAssignment;
import com.rooftop.delivery.model.Driver;
import com.rooftop.delivery.repository.CustomerRepository;
import com.rooftop.delivery.repository.DeliveryAssignmentRepository;
import com.rooftop.delivery.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@Service
public class DeliveryAssignmentService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private DriverRepository driverRepository;
    
    @Autowired
    private DeliveryAssignmentRepository assignmentRepository;
    @Transactional
    public List<AssignmentResponse> processBatchOrders(BatchOrderRequest request) {
        initializeDrivers(request.getNumberOfDrivers());
        
        List<AssignmentResponse> responses = new ArrayList<>();
        List<Driver> drivers = driverRepository.findAllByOrderByDriverIdAsc();
        
        // Use PriorityQueue for O(log M) driver lookup instead of O(M) iteration per order
        // Heap maintains next available driver at top, sorted by availableAt then driverId
        PriorityQueue<Driver> driverQueue = new PriorityQueue<>(
            Comparator.comparingInt(Driver::getAvailableAt)
                      .thenComparing(Driver::getDriverId)  // Ensures D1 > D2 when both free at same time
        );
        driverQueue.addAll(drivers);
        
        for (int i = 0; i < request.getOrders().size(); i++) {
            OrderRequest order = request.getOrders().get(i);
            String customerId = "C" + (i + 1);
            
            Customer customer = new Customer(customerId, order.getOrderTime(), order.getTravelTime());
            String assignedDriver = assignDriverOptimized(driverQueue, order.getOrderTime(), order.getTravelTime());
            
            customer.setAssignedDriver(assignedDriver);
            customer.setStatus(assignedDriver.equals("No Food :-(") ? "REJECTED" : "ASSIGNED");
            customerRepository.save(customer);
            
            saveAssignmentRecord(customerId, assignedDriver, order.getOrderTime(), order.getTravelTime());
            
            AssignmentResponse response = new AssignmentResponse(customerId, assignedDriver);
            responses.add(response);
        }
        
        return responses;
    }
    
    private String assignDriverOptimized(PriorityQueue<Driver> driverQueue, int orderTime, int travelTime) {
        Driver selectedDriver = driverQueue.peek();
        
        if (selectedDriver == null || selectedDriver.getAvailableAt() > orderTime) {
            return "No Food :-(";
        }
        
        // Remove from heap, update availability, then re-insert to maintain heap property
        driverQueue.poll();
        selectedDriver.setStatus("Busy");
        selectedDriver.setAvailableAt(orderTime + travelTime);
        driverRepository.save(selectedDriver);
        
        driverQueue.offer(selectedDriver);  // Re-heap with updated time
        
        return selectedDriver.getDriverId();
    }
    
    private void initializeDrivers(int numberOfDrivers) {
        driverRepository.deleteAll();
        
        for (int i = 1; i <= numberOfDrivers; i++) {
            Driver driver = new Driver("D" + i);
            driverRepository.save(driver);
        }
    }
    
    private void saveAssignmentRecord(String customerId, String assignedDriver, 
                                     int orderTime, int travelTime) {
        DeliveryAssignment assignment = new DeliveryAssignment();
        assignment.setCustomerId(customerId);
        assignment.setOrderTime(orderTime);
        assignment.setAssignmentResult(assignedDriver);
        
        // Only set driver details if assignment was successful
        if (!assignedDriver.equals("No Food :-(")) {
            assignment.setDriverId(assignedDriver);
            assignment.setAssignmentTime(orderTime);
            assignment.setCompletionTime(orderTime + travelTime);
        }
        
        assignmentRepository.save(assignment);
    }
    
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }
    
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
    
    public List<DeliveryAssignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }
    
    @Transactional
    public void resetSystem() {
        assignmentRepository.deleteAll();
        customerRepository.deleteAll();
        driverRepository.deleteAll();
    }
}
