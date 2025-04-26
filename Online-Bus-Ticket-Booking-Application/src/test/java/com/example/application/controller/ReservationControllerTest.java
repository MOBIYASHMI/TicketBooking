package com.example.application.controller;

import com.example.application.dto.BusDto;
import com.example.application.dto.ScheduleDto;
import com.example.application.entity.User;
import com.example.application.exceptions.BusNotFoundException;
import com.example.application.exceptions.ReservationNotFoundException;
import com.example.application.exceptions.ScheduleNotFoundException;
import com.example.application.exceptions.SeatAlreadyBookedException;
import com.example.application.repository.UserRepository;
import com.example.application.service.BusService;
import com.example.application.service.ReservationService;
import com.example.application.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private BusService busService;

    @Mock
    private ScheduleService scheduleService;

    @Mock
    private ReservationService reservationService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationController reservationController;

    @BeforeEach
    void setUp(){
        mockMvc= MockMvcBuilders.standaloneSetup(reservationController).build();
    }

    @Test
    public void testGetBusList() throws Exception {
        LocalDate date = LocalDate.now();
        when(scheduleService.findAvailableSchedules("Chennai", "Bangalore", date))
                .thenReturn(Collections.singletonList(new ScheduleDto()));

        mockMvc.perform(get("/reservation/buslist")
                        .param("source", "Chennai")
                        .param("destination", "Bangalore")
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("userBusList"))
                .andExpect(model().attributeExists("busSchedules"));
    }

    @Test
    public void testGetBusList_Exception() throws Exception {
        LocalDate date = LocalDate.now();
        doThrow(new ScheduleNotFoundException("Schedule not found")).when(scheduleService).findAvailableSchedules("Chennai", "Bangalore", date);

        mockMvc.perform(get("/reservation/buslist")
                        .param("source", "Chennai")
                        .param("destination", "Bangalore")
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    public void testShowSeatSelection() throws Exception {
        BusDto busDto = new BusDto(1L, "Intrcity", "AC", "A123", 40, null);
        ScheduleDto scheduleDto = new ScheduleDto();
        when(busService.getByBusId(1L)).thenReturn(busDto);
        when(scheduleService.getScheduleById(1L)).thenReturn(scheduleDto);
        when(reservationService.getBookedSeats(1L, 1L)).thenReturn(List.of(1, 2, 3));

        mockMvc.perform(get("/reservation/seats")
                        .param("busId", "1")
                        .param("scheduleId", "1")
                        .param("source", "Chennai")
                        .param("destination", "Bangalore")
                        .param("date", "2025-04-23"))
                .andExpect(status().isOk())
                .andExpect(view().name("seatSelection"))
                .andExpect(model().attributeExists("availableSeats"))
                .andExpect(model().attributeExists("allSeats"));
    }

    @Test
    public void testShowSeatSelection_BusNotFoundException() throws Exception {
        when(busService.getByBusId(1L)).thenThrow(new BusNotFoundException("Bus not found"));

        mockMvc.perform(get("/reservation/seats")
                        .param("busId", "1")
                        .param("scheduleId", "1")
                        .param("source", "Chennai")
                        .param("destination", "Bangalore")
                        .param("date", "2025-04-23"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Bus not found"));
    }

    @Test
    public void testShowSeatSelection_ScheduleNotFoundException() throws Exception {
        BusDto busDto = new BusDto(1L, "Intrcity", "AC", "TN01", 40, null);
        when(busService.getByBusId(1L)).thenReturn(busDto);
        when(scheduleService.getScheduleById(1L)).thenThrow(new ScheduleNotFoundException("Schedule not found"));

        mockMvc.perform(get("/reservation/seats")
                        .param("busId", "1")
                        .param("scheduleId", "1")
                        .param("source", "Chennai")
                        .param("destination", "Bangalore")
                        .param("date", "2025-04-23"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Schedule not found"));
    }

    @Test
    public void testConfirmBooking() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken("testuser", "password");
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        User user = new User();
        user.setId(1L);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        doNothing().when(reservationService).bookSeats(anyLong(), anyLong(), anyList(), eq(1L));

        mockMvc.perform(post("/reservation/confirm")
                        .param("busId", "1")
                        .param("scheduleId", "1")
                        .param("selectedSeats", "4,5")
                        .param("source", "Chennai")
                        .param("destination", "Bangalore")
                        .param("date", "2025-04-23"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservation/reservations"));
    }

    @Test
    public void testConfirmBooking_BusNotFoundException() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken("testuser", "password");
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        User user = new User();
        user.setId(1L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        doThrow(new BusNotFoundException("Bus not found")).when(reservationService).bookSeats(anyLong(), anyLong(), anyList(), eq(1L));

        mockMvc.perform(post("/reservation/confirm")
                        .param("busId", "1")
                        .param("scheduleId", "1")
                        .param("selectedSeats", "4,5")
                        .param("source", "Chennai")
                        .param("destination", "Bangalore")
                        .param("date", "2025-04-23"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservation/seats?busId=" + 1L + "&scheduleId=" + 1L +
                        "&source=" + "Chennai" + "&destination=" + "Bangalore" + "&date=" + "2025-04-23"));
    }

    @Test
    public void testConfirmBooking_ScheduleNotFoundException() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken("testuser", "password");
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        User user = new User();
        user.setId(1L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        doThrow(new ScheduleNotFoundException("Schedule not found")).when(reservationService).bookSeats(anyLong(), anyLong(), anyList(), eq(1L));

        mockMvc.perform(post("/reservation/confirm")
                        .param("busId", "1")
                        .param("scheduleId", "1")
                        .param("selectedSeats", "4,5")
                        .param("source", "Chennai")
                        .param("destination", "Bangalore")
                        .param("date", "2025-04-23"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservation/seats?busId=" + 1L + "&scheduleId=" + 1L +
                        "&source=" + "Chennai" + "&destination=" + "Bangalore" + "&date=" + "2025-04-23"));
    }

    @Test
    public void testConfirmBooking_SeatAlreadyBookedException() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken("testuser", "password");
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        User user = new User();
        user.setId(1L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        doThrow(new SeatAlreadyBookedException("Seat already booked")).when(reservationService).bookSeats(anyLong(), anyLong(), anyList(), eq(1L));

        mockMvc.perform(post("/reservation/confirm")
                        .param("busId", "1")
                        .param("scheduleId", "1")
                        .param("selectedSeats", "4,5")
                        .param("source", "Chennai")
                        .param("destination", "Bangalore")
                        .param("date", "2025-04-23"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservation/seats?busId=" + 1L + "&scheduleId=" + 1L +
                        "&source=" + "Chennai" + "&destination=" + "Bangalore" + "&date=" + "2025-04-23"));
    }

    @Test
    public void testConfirmBooking_UserNotFoundException() throws Exception {
        String mockUsername = "nonexistentuser";
        Authentication auth = new UsernamePasswordAuthenticationToken(mockUsername, "password");
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Mock userRepository to return empty (user not found)
        when(userRepository.findByUsername(mockUsername)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/reservation/confirm")
                        .param("busId", "1")
                        .param("scheduleId", "1")
                        .param("selectedSeats", "1,2")
                        .param("source", "Chennai")
                        .param("destination", "Bangalore")
                        .param("date", "2025-04-23"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"))
                .andExpect(flash().attribute("errorMessage", "User not found. Please log in again."));

        // Clean up context
        SecurityContextHolder.clearContext();
    }

//    @Test
//    @WithMockUser(username = "testuser", roles = {"USER"})
//    public void testViewUserReservations() throws Exception {
//        Authentication auth = new UsernamePasswordAuthenticationToken("testuser", "password");
//        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
//        securityContext.setAuthentication(auth);
//        SecurityContextHolder.setContext(securityContext);
//
//        User user = new User();
//        user.setId(1L);
//        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
//        when(reservationService.getUserReservations(1L)).thenReturn(Collections.singletonList(new ReservationDto()));
//
//        mockMvc.perform(get("/reservation/reservations").with(authentication(auth)))
//                .andExpect(status().isOk())
//                .andExpect(view().name("userReservations"))
//                .andExpect(model().attributeExists("reservations"));
//    }

    @Test
    @WithMockUser
    public void testCancelReservationSuccess() throws Exception {
        doNothing().when(reservationService).cancelReservation(1L);

        mockMvc.perform(post("/reservation/cancel")
                        .param("reservationId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservation/reservations"));
    }

    @Test
    @WithMockUser
    public void testCancelReservation_ReservationNotFound() throws Exception {
        doThrow(new ReservationNotFoundException("Reservation not found")).when(reservationService).cancelReservation(1L);

        mockMvc.perform(post("/reservation/cancel")
                        .param("reservationId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservation/reservations"));
    }


}
