package com.example.application.service.impl;

import com.example.application.dto.BusDto;
import com.example.application.dto.ScheduleDto;
import com.example.application.entity.Bus;
import com.example.application.entity.Schedule;
import com.example.application.exceptions.ScheduleNotFoundException;
import com.example.application.repository.BusRepository;
import com.example.application.repository.ScheduleRepository;
import com.example.application.service.ScheduleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BusRepository busRepository;

    @Override
    public List<ScheduleDto> getAllSchedules() {
        List<Schedule> schedules=scheduleRepository.findAll();
        return schedules.stream().map(schedule -> modelMapper.map(schedule, ScheduleDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDto> findAvailableSchedules(String source, String destination, LocalDate departureDate) {
        List<Schedule> schedules = scheduleRepository.findBySourceAndDestinationAndScheduledDate(source, destination, departureDate);
        return schedules.stream()
                .map(schedule ->{
                    ScheduleDto scheduleDto=modelMapper.map(schedule,ScheduleDto.class);
                    Bus bus=busRepository.findById(schedule.getBus().getId()).orElse(null);
                    if (bus!=null){
                        scheduleDto.setBus(modelMapper.map(bus, BusDto.class));
                    }
                    return scheduleDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleDto getScheduleById(Long scheduleId) {
        Schedule schedule=scheduleRepository.findById(scheduleId).orElseThrow(()-> new ScheduleNotFoundException("Schedule not found"));
        return modelMapper.map(schedule,ScheduleDto.class);
    }
}
