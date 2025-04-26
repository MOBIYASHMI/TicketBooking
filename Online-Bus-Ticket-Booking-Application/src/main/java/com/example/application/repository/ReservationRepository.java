package com.example.application.repository;

import com.example.application.entity.Bus;
import com.example.application.entity.Reservation;
import com.example.application.entity.Schedule;
import com.example.application.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    List<Reservation> findByBusAndScheduleAndSeatNumbersIn(Bus bus, Schedule schedule, List<Integer> seatNumbers);

    List<Reservation> findBySchedule(Schedule schedule);

    List<Reservation> findByUser(User user);

    List<Reservation> findByBusAndSchedule(Bus bus,Schedule schedule);
}
