package com.rooftop.delivery.repository;

import com.rooftop.delivery.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Driver findByDriverId(String driverId);
    List<Driver> findAllByOrderByDriverIdAsc();
}
