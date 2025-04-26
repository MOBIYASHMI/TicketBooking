package com.example.application.service;

import com.example.application.dto.ScheduleDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {

    List<ScheduleDto> getAllSchedules();
    List<ScheduleDto> findAvailableSchedules(String source, String destination, LocalDate departureDate);

    ScheduleDto  getScheduleById(Long scheduleId);
}
