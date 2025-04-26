package com.example.application.service;

import com.example.application.dto.BusDto;
import com.example.application.dto.ScheduleDto;
import com.example.application.entity.Bus;
import com.example.application.entity.Schedule;
import com.example.application.exceptions.BusAlreadyExistsException;
import com.example.application.exceptions.BusNotFoundException;
import com.example.application.repository.BusRepository;
import com.example.application.repository.ScheduleRepository;
import com.example.application.service.impl.BusServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BusServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BusRepository busRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private BusServiceImpl busService;

    private BusDto busDto;
    private Bus bus;
    private ScheduleDto scheduleDto;
    private Schedule schedule;

    @BeforeEach
    void setUp(){
        busDto = new BusDto(1L,"Universe","AC Seater","8453",40,null);
        bus=new Bus(1L,"Universe","AC Seater",40,"8736",null);
        scheduleDto=new ScheduleDto(1L,1L,"CityA","CityB", LocalDate.now(),"10:00","10:30",400d,40);
        schedule=new Schedule(1L,bus,"CityA", LocalDate.now(),"CityB", LocalTime.now(),LocalTime.now(),400d,40);
    }

    @Test
    void testCreateBusWithSchedules_WhenBusIsNew() {
        busDto.setSchedules(List.of(scheduleDto));

        when(busRepository.findByBusName("Universe")).thenReturn(Optional.empty());
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Bus.class), eq(BusDto.class))).thenReturn(busDto);

        BusDto createdBusDto = busService.createBusWithSchedules(busDto);

        assertThat(createdBusDto).isNotNull();
        assertThat(createdBusDto.getBusName()).isEqualTo("Universe");

        verify(busRepository, times(1)).findByBusName("Universe");
        verify(busRepository, times(2)).save(any(Bus.class)); // Once before and once after schedule addition
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
        verify(modelMapper, times(1)).map(any(Bus.class), eq(BusDto.class));
    }

    @Test
    void testCreateBusWithSchedules_WhenBusAlreadyExists_ShouldThrowException() {
        BusDto busDto = new BusDto();
        busDto.setBusName("Universe");

        when(busRepository.findByBusName("Universe")).thenReturn(Optional.of(new Bus()));

        assertThrows(BusAlreadyExistsException.class, () -> {
            busService.createBusWithSchedules(busDto);
        });

        verify(busRepository, times(1)).findByBusName("Universe");
        verify(busRepository, never()).save(any());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    public void testGetAllBuses(){
        Bus bus1=new Bus(1L,"Universe","AC Seater",40,"8736",null);
        Bus bus2=new Bus(2L,"A1","AC Sleeper",40,"8738",null);

        BusDto busDto1=new BusDto(1L,"Universe","AC Seater","8453",40,null);
        BusDto busDto2=new BusDto(2L,"A1","AC Sleeper","8454",40,null);

        List<Bus> buses=List.of(bus1,bus2);

        when(busRepository.findAll()).thenReturn(buses);
        when(modelMapper.map(bus1, BusDto.class)).thenReturn(busDto1);
        when(modelMapper.map(bus2, BusDto.class)).thenReturn(busDto2);

        List<BusDto> result = busService.getAllBuses();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getBusName()).isEqualTo("Universe");
        assertThat(result.get(1).getBusName()).isEqualTo("A1");
    }

    @Test
    public void testGetByBusId(){
        Long busId=1L;
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(modelMapper.map(bus,BusDto.class)).thenReturn(busDto);

        BusDto result = busService.getByBusId(busId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(busId);
        assertThat(result.getBusName()).isEqualTo("Universe");

        verify(busRepository, times(1)).findById(busId);
        verify(modelMapper, times(1)).map(bus, BusDto.class);
    }

    @Test
    public void testGetByBusId_BusNotFoundException(){
        Long busId = 99L;
        when(busRepository.findById(busId)).thenReturn(Optional.empty());

        assertThrows(BusNotFoundException.class, () -> busService.getByBusId(busId));

        verify(busRepository, times(1)).findById(busId);
        verify(modelMapper, never()).map(any(), eq(BusDto.class));
    }

    @Test
    void testUpdateExistingSchedules_WhenSchedulesExist_ShouldUpdateSchedules() {
        Long busId = 1L;
        Long scheduleId = 100L;

        Bus existingBus = new Bus();
        existingBus.setId(busId);

        Schedule existingSchedule = new Schedule();
        existingSchedule.setId(scheduleId);
        existingSchedule.setBus(existingBus);

        ScheduleDto updatedScheduleDto = new ScheduleDto();
        updatedScheduleDto.setId(scheduleId);
        updatedScheduleDto.setSource("City A");
        updatedScheduleDto.setDestination("City B");
        updatedScheduleDto.setScheduledDate(LocalDate.of(2025, 5, 10));
        updatedScheduleDto.setArrivalTime("10:00");
        updatedScheduleDto.setDepartureTime("08:00");
        updatedScheduleDto.setPrice(450.0);
        updatedScheduleDto.setAvailableSeats(30);

        BusDto busDto = new BusDto();
        busDto.setId(busId);
        busDto.setSchedules(List.of(updatedScheduleDto));

        when(busRepository.findById(busId)).thenReturn(Optional.of(existingBus));
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(existingSchedule));

        busService.updateExistingSchedules(busDto);

        assertThat(existingSchedule.getSource()).isEqualTo("City A");
        assertThat(existingSchedule.getDestination()).isEqualTo("City B");
        assertThat(existingSchedule.getPrice()).isEqualTo(450.0);
        verify(scheduleRepository, times(1)).save(existingSchedule);
    }

    @Test
    void testUpdateExistingSchedules_WhenBusNotFound_Exception() {
        Long busId = 999L;
        BusDto busDto = new BusDto();
        busDto.setId(busId);

        when(busRepository.findById(busId)).thenReturn(Optional.empty());

        assertThrows(BusNotFoundException.class, () -> busService.updateExistingSchedules(busDto));
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    void testDeleteBusAndSchedules_WhenBusExists_ShouldDeleteBusAndSchedules() {
        Long busId = 1L;
        Bus existingBus = new Bus();
        existingBus.setId(busId);
        existingBus.setBusName("Express");

        when(busRepository.findById(busId)).thenReturn(Optional.of(existingBus));

        busService.deleteBusAndSchedules(busId);

        verify(scheduleRepository, times(1)).deleteByBus(existingBus);
        verify(busRepository, times(1)).delete(existingBus);
    }

    @Test
    void testDeleteBusAndSchedules_WhenBusNotFound_ShouldThrowException() {
        Long busId = 999L;
        when(busRepository.findById(busId)).thenReturn(Optional.empty());

        assertThrows(BusNotFoundException.class, () -> busService.deleteBusAndSchedules(busId));

        verify(scheduleRepository, never()).deleteByBus(any());
        verify(busRepository, never()).delete(any());
    }

    @Test
    public void testViewByBusType(){
        String busType = "AC Seater";

        Bus bus1=new Bus(1L,"Universe","AC Seater",40,"8736",null);
        Bus bus2=new Bus(2L,"A1","AC Seater",40,"8738",null);

        List<Bus> busList = List.of(bus1, bus2);

        BusDto busDto1=new BusDto(1L,"Universe","AC Seater","8453",40,null);
        BusDto busDto2=new BusDto(2L,"A1","AC Seater","8454",40,null);

        when(busRepository.findByBusType(busType)).thenReturn(busList);
        when(modelMapper.map(bus1, BusDto.class)).thenReturn(busDto1);
        when(modelMapper.map(bus2, BusDto.class)).thenReturn(busDto2);

        List<BusDto> result = busService.viewByBusType(busType);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getBusName()).isEqualTo("Universe");
        assertThat(result.get(1).getBusName()).isEqualTo("A1");

        verify(busRepository, times(1)).findByBusType(busType);
        verify(modelMapper, times(1)).map(bus1, BusDto.class);
        verify(modelMapper, times(1)).map(bus2, BusDto.class);
    }

    @Test
    void testAddNewSchedules_ShouldAddOnlyNewSchedules() {
        Long busId = 1L;
        Bus bus = new Bus();
        bus.setId(busId);
        bus.setBusName("Super Bus");

        ScheduleDto newScheduleDto = new ScheduleDto();
        newScheduleDto.setId(null); // New schedule (no ID)
        newScheduleDto.setSource("City A");
        newScheduleDto.setDestination("City B");
        newScheduleDto.setScheduledDate(LocalDate.now());
        newScheduleDto.setArrivalTime("10:00");
        newScheduleDto.setDepartureTime("08:00");
        newScheduleDto.setPrice(500.0);
        newScheduleDto.setAvailableSeats(30);

        BusDto busDto = new BusDto();
        busDto.setId(busId);
        busDto.setSchedules(List.of(newScheduleDto));

        when(busRepository.findById(busId)).thenReturn(Optional.of(bus));

        busService.addNewSchedules(busDto);

        ArgumentCaptor<List<Schedule>> scheduleCaptor = ArgumentCaptor.forClass(List.class);
        verify(scheduleRepository, times(1)).saveAll(scheduleCaptor.capture());

        List<Schedule> capturedSchedules = scheduleCaptor.getValue();
        assertThat(capturedSchedules).hasSize(1);
        assertThat(capturedSchedules.get(0).getSource()).isEqualTo("City A");
        assertThat(capturedSchedules.get(0).getDestination()).isEqualTo("City B");
        assertThat(capturedSchedules.get(0).getArrivalTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(capturedSchedules.get(0).getDepartureTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(capturedSchedules.get(0).getBus()).isEqualTo(bus);

        verify(busRepository, times(1)).findById(busId);
    }

    @Test
    void testAddNewSchedules_ShouldThrowException_WhenBusNotFound() {
        Long busId = 999L;
        BusDto busDto = new BusDto();
        busDto.setId(busId);
        when(busRepository.findById(busId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> busService.addNewSchedules(busDto))
                .isInstanceOf(BusNotFoundException.class)
                .hasMessage("Bus not found with ID: " + busId);

        verify(scheduleRepository, never()).saveAll(any());
    }

    @Test
    void testAddNewSchedules_ShouldIgnoreSchedulesWithExistingIds() {
        Long busId = 1L;
        Bus bus = new Bus();
        bus.setId(busId);

        ScheduleDto existingScheduleDto = new ScheduleDto();
        existingScheduleDto.setId(10L); // Existing schedule ID
        existingScheduleDto.setSource("City A");
        existingScheduleDto.setDestination("City B");

        BusDto busDto = new BusDto();
        busDto.setId(busId);
        busDto.setSchedules(List.of(existingScheduleDto));

        when(busRepository.findById(busId)).thenReturn(Optional.of(bus));

        busService.addNewSchedules(busDto);

        verify(busRepository, times(1)).findById(busId);
    }
}