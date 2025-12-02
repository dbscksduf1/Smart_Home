package com.example.smart.repository;

import com.example.smart.domain.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<SensorData, Long> {
}
