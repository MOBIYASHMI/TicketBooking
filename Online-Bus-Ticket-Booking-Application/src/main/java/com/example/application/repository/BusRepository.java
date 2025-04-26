package com.example.application.repository;

import com.example.application.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus,Long> {
    List<Bus> findByBusType(String busType);
    Optional<Bus> findByBusName(String busName);
//    Optional<Bus> findByBusId(Long busId);
}
