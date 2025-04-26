package com.example.application.service.impl;

import com.example.application.dto.BusDto;
import com.example.application.dto.ScheduleDto;
import com.example.application.entity.Bus;
import com.example.application.entity.Schedule;
import com.example.application.exceptions.BusAlreadyExistsException;
import com.example.application.exceptions.BusNotFoundException;
import com.example.application.repository.BusRepository;
import com.example.application.repository.ScheduleRepository;
import com.example.application.service.BusService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusServiceImpl implements BusService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public BusDto createBusWithSchedules(BusDto busDto) {
        Optional<Bus> existingBus=busRepository.findByBusName(busDto.getBusName());
        if (existingBus.isPresent()) {
            throw new BusAlreadyExistsException("Bus with name '" + busDto.getBusName() + "' already exists.");
        }

        Bus bus=new Bus();
        bus.setId(busDto.getId());
        bus.setBusName(busDto.getBusName());
        bus.setBusType(busDto.getBusType());
        bus.setTotalSeats(busDto.getTotalSeats());
        bus.setRegistrationNumber(busDto.getRegistrationNumber());

        Bus savedBus=busRepository.save(bus);

        for (ScheduleDto scheduleDto: busDto.getSchedules()){
            Schedule schedule = convertScheduleDto(scheduleDto, savedBus);
            schedule.setBus(bus);
            scheduleRepository.save(schedule);
        }

        return modelMapper.map(busRepository.save(bus),BusDto.class);
    }

    private static Schedule convertScheduleDto(ScheduleDto scheduleDto, Bus savedBus) {
        Schedule schedule=new Schedule();
        schedule.setId(scheduleDto.getId());
        schedule.setBus(savedBus);
        schedule.setScheduledDate(scheduleDto.getScheduledDate());
        schedule.setDepartureTime(LocalTime.parse(scheduleDto.getDepartureTime()));
        schedule.setArrivalTime(LocalTime.parse(scheduleDto.getArrivalTime()));
        schedule.setSource(scheduleDto.getSource());
        schedule.setDestination(scheduleDto.getDestination());
        schedule.setPrice(scheduleDto.getPrice());
        schedule.setAvailableSeats(scheduleDto.getAvailableSeats());
        return schedule;
    }

    @Override
    public List<BusDto> getAllBuses() {
        List<Bus> buses=busRepository.findAll();
        return buses.stream().map(bus -> modelMapper.map(bus,BusDto.class)).collect(Collectors.toList());
    }

    @Override
    public BusDto getByBusId(Long busId) {
        Bus bus=busRepository.findById(busId).orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + busId));
        return modelMapper.map(bus,BusDto.class);
    }

    @Transactional
    @Override
    public void updateExistingSchedules(BusDto busDto) throws BusNotFoundException {
        Bus existingBus = busRepository.findById(busDto.getId())
                .orElseThrow(() -> new BusNotFoundException("Bus not found with ID: " + busDto.getId()));

        if (busDto.getSchedules() != null) {
            for (ScheduleDto scheduleDto : busDto.getSchedules()) {
                System.out.println("Schedule ID: " + scheduleDto.getId());
                if (scheduleDto.getId() != null && scheduleDto.getId() > 0) {
                    // Only update existing schedules (with an ID)
                    Schedule schedule = scheduleRepository.findById(scheduleDto.getId())
                            .orElse(null);
                    if (schedule != null) {
                        schedule.setSource(scheduleDto.getSource());
                        schedule.setDestination(scheduleDto.getDestination());
                        schedule.setScheduledDate(scheduleDto.getScheduledDate());
                        schedule.setArrivalTime(LocalTime.parse(scheduleDto.getArrivalTime()));
                        schedule.setDepartureTime(LocalTime.parse(scheduleDto.getDepartureTime()));
                        schedule.setPrice(scheduleDto.getPrice());
                        schedule.setAvailableSeats(scheduleDto.getAvailableSeats());
                        schedule.setBus(existingBus);
                    }
                    scheduleRepository.save(schedule);
                }
            }
        }
    }

    @Transactional
    @Override
    public void deleteBusAndSchedules(Long busId) throws BusNotFoundException {
        Bus existingBus = busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException("Bus not found with ID: " + busId));

        // Delete all schedules associated with the bus
        scheduleRepository.deleteByBus(existingBus);

        // Delete the bus itself
        busRepository.delete(existingBus);
    }

    @Override
    public List<BusDto> viewByBusType(String busType) {
        List<Bus> buses=busRepository.findByBusType(busType);
        return buses.stream().map(bus -> modelMapper.map(bus,BusDto.class)).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void addNewSchedules(BusDto busDto) throws BusNotFoundException {
        Bus existingBus = busRepository.findById(busDto.getId())
                .orElseThrow(() -> new BusNotFoundException("Bus not found with ID: " + busDto.getId()));

        List<Schedule> newSchedules = new ArrayList<>();
        if (busDto.getSchedules() != null) {
            for (ScheduleDto scheduleDto : busDto.getSchedules()) {
                // Only save schedules without an ID (newly added)
                if (scheduleDto.getId() == null || scheduleDto.getId() == 0) {
                    Schedule schedule = new Schedule();
                    BeanUtils.copyProperties(scheduleDto, schedule);
                    schedule.setArrivalTime(LocalTime.parse(scheduleDto.getArrivalTime()));
                    schedule.setDepartureTime(LocalTime.parse(scheduleDto.getDepartureTime()));
                    schedule.setBus(existingBus);
                    newSchedules.add(schedule);
                }
            }
        }
        scheduleRepository.saveAll(newSchedules);
    }
}
