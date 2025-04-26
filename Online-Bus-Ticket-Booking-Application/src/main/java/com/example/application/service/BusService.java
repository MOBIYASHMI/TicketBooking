package com.example.application.service;

import com.example.application.dto.BusDto;

import java.util.List;

public interface BusService {

    List<BusDto> getAllBuses();

    BusDto getByBusId(Long busId);

    BusDto createBusWithSchedules(BusDto busDto);

    void updateExistingSchedules(BusDto busDto);

    void deleteBusAndSchedules(Long busId);

    List<BusDto> viewByBusType(String busType);

    void addNewSchedules(BusDto busDto);

}
