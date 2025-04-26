package com.example.application.repository;

import com.example.application.entity.Bus;
import com.example.application.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Long> {

    void deleteByBus(Bus existingBus);
    List<Schedule> findBySourceAndDestinationAndScheduledDate(String source, String destination, LocalDate scheduledDate);
}
