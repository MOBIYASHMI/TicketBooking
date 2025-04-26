package com.example.application.service;

import com.example.application.dto.BusDto;
import com.example.application.dto.ScheduleDto;
import com.example.application.entity.Bus;
import com.example.application.entity.Role;
import com.example.application.entity.Schedule;
import com.example.application.entity.User;
import com.example.application.exceptions.ScheduleNotFoundException;
import com.example.application.repository.BusRepository;
import com.example.application.repository.ScheduleRepository;
import com.example.application.service.impl.ScheduleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceImplTest {
    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BusRepository busRepository;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    private BusDto busDto;
    private Bus bus;
    private ScheduleDto scheduleDto;
    private Schedule schedule;
    private User user;

    @BeforeEach
    void setUp(){
        user=new User(1L,"test","test@gmail.com","12345678", Role.USER);
        busDto = new BusDto(1L,"Universe","AC Seater","8453",40,null);
        bus=new Bus(1L,"Universe","AC Seater",40,"8736",null);
        scheduleDto=new ScheduleDto(1L,1L,"CityA","CityB", LocalDate.now(),"10:00","10:30",400d,40);
        schedule=new Schedule(1L,bus,"CityA", LocalDate.now(),"CityB", LocalTime.now(),LocalTime.now(),400d,40);
    }

    @Test
    public void testGetAllSchedules(){
        Schedule schedule1 = new Schedule(1L,bus,"CityA", LocalDate.now(),"CityB", LocalTime.now(),LocalTime.now(),400d,40);
        Schedule schedule2 = new Schedule(2L,bus,"CityA", LocalDate.now(),"CityB", LocalTime.now(),LocalTime.now(),400d,40);

        List<Schedule> schedules = Arrays.asList(schedule1, schedule2);
        ScheduleDto scheduleDto1 = new ScheduleDto(1L,1L,"CityA","CityB", LocalDate.now(),"10:00","10:30",400d,40);
        ScheduleDto scheduleDto2 = new ScheduleDto(1L,1L,"CityA","CityB", LocalDate.now(),"10:00","10:30",400d,40);

        when(scheduleRepository.findAll()).thenReturn(schedules);

        when(modelMapper.map(schedule1, ScheduleDto.class)).thenReturn(scheduleDto1);
        when(modelMapper.map(schedule2, ScheduleDto.class)).thenReturn(scheduleDto2);

        List<ScheduleDto> result = scheduleService.getAllSchedules();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(scheduleRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(schedule1, ScheduleDto.class);
        verify(modelMapper, times(1)).map(schedule2, ScheduleDto.class);
    }

    @Test
    public void testFindAvailableSchedules() {
        String source = "CityA";
        String destination = "CityB";
        LocalDate departureDate = LocalDate.now();

        Schedule schedule1 = new Schedule(1L, bus, source, departureDate, destination, LocalTime.of(10, 0), LocalTime.of(12, 0), 400.0, 40);
        Schedule schedule2 = new Schedule(2L, bus, source, departureDate, destination, LocalTime.of(13, 0), LocalTime.of(15, 0), 420.0, 40);

        List<Schedule> schedules = Arrays.asList(schedule1, schedule2);

        ScheduleDto scheduleDto1 = new ScheduleDto();
        ScheduleDto scheduleDto2 = new ScheduleDto();

        when(scheduleRepository.findBySourceAndDestinationAndScheduledDate(source, destination, departureDate)).thenReturn(schedules);
        when(modelMapper.map(schedule1, ScheduleDto.class)).thenReturn(scheduleDto1);
        when(modelMapper.map(schedule2, ScheduleDto.class)).thenReturn(scheduleDto2);
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(modelMapper.map(bus, BusDto.class)).thenReturn(busDto);

        List<ScheduleDto> result = scheduleService.findAvailableSchedules(source, destination, departureDate);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(busDto, result.get(0).getBus());
        assertEquals(busDto, result.get(1).getBus());

        verify(scheduleRepository, times(1)).findBySourceAndDestinationAndScheduledDate(source, destination, departureDate);
        verify(busRepository, times(2)).findById(1L);
        verify(modelMapper, times(2)).map(any(Schedule.class), eq(ScheduleDto.class));
        verify(modelMapper, times(2)).map(eq(bus), eq(BusDto.class));
    }

    @Test
    public void testGetScheduleById_Success() {
        Long scheduleId = 1L;

        Schedule schedule = new Schedule();
        schedule.setId(scheduleId);

        ScheduleDto scheduleDto = new ScheduleDto();
        scheduleDto.setId(scheduleId);

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        when(modelMapper.map(schedule, ScheduleDto.class)).thenReturn(scheduleDto);

        ScheduleDto result = scheduleService.getScheduleById(scheduleId);

        assertNotNull(result);
        assertEquals(scheduleId, result.getId());

        verify(scheduleRepository, times(1)).findById(scheduleId);
        verify(modelMapper, times(1)).map(schedule, ScheduleDto.class);
    }

    @Test
    public void testGetScheduleById_NotFound() {
        Long scheduleId = 99L;

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

        assertThrows(ScheduleNotFoundException.class, () -> {
            scheduleService.getScheduleById(scheduleId);
        });

        verify(scheduleRepository, times(1)).findById(scheduleId);
        verify(modelMapper, never()).map(any(), eq(ScheduleDto.class));
    }

}