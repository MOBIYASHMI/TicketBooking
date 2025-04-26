package com.example.application.service;

import com.example.application.dto.BusDto;
import com.example.application.dto.ReservationDto;
import com.example.application.dto.ScheduleDto;
import com.example.application.entity.*;
import com.example.application.exceptions.*;
import com.example.application.repository.BusRepository;
import com.example.application.repository.ReservationRepository;
import com.example.application.repository.ScheduleRepository;
import com.example.application.repository.UserRepository;
import com.example.application.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplTest {
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BusRepository busRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

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
    void testBookSeats_SuccessfulBooking_WhenSeatsAreAvailable() {
        // Arrange
        Long busId = 1L;
        Long scheduleId = 1L;
        Long userId = 1L;
        List<Integer> seatNumbers = List.of(5, 6);

        Bus bus = new Bus();
        bus.setId(busId);

        Schedule schedule = new Schedule();
        schedule.setId(scheduleId);
        schedule.setAvailableSeats(40);

        User user = new User();
        user.setId(userId);

        when(busRepository.findById(busId)).thenReturn(Optional.of(bus));
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reservationRepository.findByBusAndScheduleAndSeatNumbersIn(bus, schedule, seatNumbers))
                .thenReturn(Collections.emptyList());

        reservationService.bookSeats(busId, scheduleId, seatNumbers, userId);

        ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository, times(1)).save(reservationCaptor.capture());
        Reservation savedReservation = reservationCaptor.getValue();

        assertThat(savedReservation.getBus()).isEqualTo(bus);
        assertThat(savedReservation.getSchedule()).isEqualTo(schedule);
        assertThat(savedReservation.getUser()).isEqualTo(user);
        assertThat(savedReservation.getSeatNumbers()).containsExactlyInAnyOrder(5, 6);

        verify(scheduleRepository, times(1)).save(schedule);
        assertThat(schedule.getAvailableSeats()).isEqualTo(38);
    }

    @Test
    void testBookSeats_ThrowsException_WhenBusNotFound() {
        Long busId = 1L;
        when(busRepository.findById(busId)).thenReturn(Optional.empty());

        assertThrows(BusNotFoundException.class, () ->
                reservationService.bookSeats(busId, 2L, List.of(1, 2), 3L)
        );

        verify(busRepository, times(1)).findById(busId);
    }

    @Test
    void testBookSeats_ThrowsException_WhenScheduleNotFound() {
        Long busId = 1L, scheduleId = 2L;
        Bus bus = new Bus();
        when(busRepository.findById(busId)).thenReturn(Optional.of(bus));
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

        assertThrows(ScheduleNotFoundException.class, () ->
                reservationService.bookSeats(busId, scheduleId, List.of(1, 2), 3L)
        );

        verify(scheduleRepository, times(1)).findById(scheduleId);
    }

    @Test
    void testBookSeats_ThrowsException_WhenUserNotFound() {
        Long busId = 1L, scheduleId = 2L, userId = 3L;
        Bus bus = new Bus();
        Schedule schedule = new Schedule();

        when(busRepository.findById(busId)).thenReturn(Optional.of(bus));
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                reservationService.bookSeats(busId, scheduleId, List.of(1, 2), userId)
        );

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testBookSeats_ThrowsException_WhenSeatAlreadyBooked() {
        Long busId = 1L, scheduleId = 2L, userId = 3L;
        List<Integer> requestedSeats = List.of(1, 2);

        Bus bus = new Bus();
        Schedule schedule = new Schedule();
        User user = new User();

        Reservation existingReservation = new Reservation();
        existingReservation.setSeatNumbers(List.of(2)); // Seat 2 already booked

        when(busRepository.findById(busId)).thenReturn(Optional.of(bus));
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reservationRepository.findByBusAndScheduleAndSeatNumbersIn(bus, schedule, requestedSeats))
                .thenReturn(List.of(existingReservation));

        SeatAlreadyBookedException exception = assertThrows(SeatAlreadyBookedException.class, () ->
                reservationService.bookSeats(busId, scheduleId, requestedSeats, userId)
        );

        assertTrue(exception.getMessage().contains("Seats already booked:"));
        assertTrue(exception.getMessage().contains("2"));
    }

    @Test
    void testGetBookedSeats_ReturnsSeatNumbersSuccessfully() {
        Long busId = 1L;
        Long scheduleId = 2L;

        Bus mockBus = new Bus();
        Schedule mockSchedule = new Schedule();

        Reservation reservation1 = new Reservation();
        reservation1.setSeatNumbers(List.of(1, 2));
        Reservation reservation2 = new Reservation();
        reservation2.setSeatNumbers(List.of(3));

        when(busRepository.findById(busId)).thenReturn(Optional.of(mockBus));
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(mockSchedule));
        when(reservationRepository.findByBusAndSchedule(mockBus, mockSchedule))
                .thenReturn(List.of(reservation1, reservation2));

        List<Integer> bookedSeats = reservationService.getBookedSeats(busId, scheduleId);

        assertEquals(3, bookedSeats.size());
        assertTrue(bookedSeats.containsAll(List.of(1, 2, 3)));

        verify(busRepository, times(1)).findById(busId);
        verify(scheduleRepository, times(1)).findById(scheduleId);
        verify(reservationRepository, times(1)).findByBusAndSchedule(mockBus, mockSchedule);
    }

    @Test
    void testGetBookedSeats_ThrowsBusNotFoundException() {
        Long busId = 1L;
        Long scheduleId = 2L;

        when(busRepository.findById(busId)).thenReturn(Optional.empty());

        assertThrows(BusNotFoundException.class, () ->
                reservationService.getBookedSeats(busId, scheduleId));

        verify(busRepository, times(1)).findById(busId);
        verify(scheduleRepository, never()).findById(any());
    }

    @Test
    void testGetBookedSeats_ThrowsScheduleNotFoundException() {
        Long busId = 1L;
        Long scheduleId = 2L;

        Bus mockBus = new Bus();
        when(busRepository.findById(busId)).thenReturn(Optional.of(mockBus));
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

        assertThrows(ScheduleNotFoundException.class, () ->
                reservationService.getBookedSeats(busId, scheduleId));

        verify(busRepository, times(1)).findById(busId);
        verify(scheduleRepository, times(1)).findById(scheduleId);
    }

    @Test
    void testGetUserReservations_Success() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Bus bus = new Bus();
        bus.setId(101L);
        bus.setBusName("Express Bus");

        Schedule schedule = new Schedule();
        schedule.setId(202L);
        schedule.setSource("City A");
        schedule.setDestination("City B");
        schedule.setScheduledDate(LocalDate.now());

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setBus(bus);
        reservation.setSchedule(schedule);
        reservation.setUser(user);
        reservation.setSeatNumbers(List.of(5, 6));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reservationRepository.findByUser(user)).thenReturn(List.of(reservation));

        List<ReservationDto> result = reservationService.getUserReservations(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBusId()).isEqualTo(101L);
        assertThat(result.get(0).getScheduleId()).isEqualTo(202L);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        assertThat(result.get(0).getBusName()).isEqualTo("Express Bus");
        assertThat(result.get(0).getScheduleDetails()).contains("City A to City B");

        verify(userRepository, times(1)).findById(userId);
        verify(reservationRepository, times(1)).findByUser(user);
    }

    @Test
    void testGetUserReservations_UserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                reservationService.getUserReservations(userId));

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void testCancelReservation_Success() {
        Long reservationId = 1L;

        Reservation reservation = new Reservation();
        Schedule schedule = new Schedule();
        schedule.setAvailableSeats(40);
        reservation.setId(reservationId);
        reservation.setSchedule(schedule);
        reservation.setSeatNumbers(List.of(5, 6, 7));

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        reservationService.cancelReservation(reservationId);

        assertEquals(43, schedule.getAvailableSeats());
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, times(1)).deleteById(reservationId);
        verify(scheduleRepository, times(1)).save(schedule);
    }

    @Test
    void testCancelReservation_ThrowsReservationNotFoundException() {
        Long reservationId = 1L;

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class, () ->
                reservationService.cancelReservation(reservationId));

        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, never()).deleteById(anyLong());
        verify(scheduleRepository, never()).save(any());
    }
}
