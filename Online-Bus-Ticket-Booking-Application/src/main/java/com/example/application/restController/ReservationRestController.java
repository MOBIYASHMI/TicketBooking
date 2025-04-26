package com.example.application.restController;

import com.example.application.dto.BusDto;
import com.example.application.dto.ReservationDto;
import com.example.application.dto.ScheduleDto;
import com.example.application.entity.User;
import com.example.application.exceptions.*;
import com.example.application.repository.UserRepository;
import com.example.application.service.BusService;
import com.example.application.service.ReservationService;
import com.example.application.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/reservations")
public class ReservationRestController {

    @Autowired
    private BusService busService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/buslist")
    public ResponseEntity<?> getBusList(@RequestParam String source,
                                        @RequestParam String destination,
                                        @RequestParam String date) {
        try {
            LocalDate departureDate = LocalDate.parse(date);
            List<ScheduleDto> schedules = scheduleService.findAvailableSchedules(source, destination, departureDate);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching bus list: " + e.getMessage());
        }
    }

    @GetMapping("/seats")
    public ResponseEntity<?> showSeatSelection(@RequestParam Long busId,
                                               @RequestParam Long scheduleId) {
        try {
            BusDto bus = busService.getByBusId(busId);
            ScheduleDto schedule = scheduleService.getScheduleById(scheduleId);

            List<Integer> bookedSeats = reservationService.getBookedSeats(busId, scheduleId);
            List<Integer> availableSeats = IntStream.rangeClosed(1, bus.getTotalSeats())
                    .boxed()
                    .filter(seat -> !bookedSeats.contains(seat))
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("bus", bus);
            response.put("schedule", schedule);
            response.put("availableSeats", availableSeats);
            response.put("totalSeats", bus.getTotalSeats());

            return ResponseEntity.ok(response);
        } catch (BusNotFoundException | ScheduleNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmBooking(@RequestParam Long busId,
                                            @RequestParam Long scheduleId,
                                            @RequestParam List<Integer> selectedSeats,
                                            @RequestParam String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        try {
            reservationService.bookSeats(busId, scheduleId, selectedSeats, user.get().getId());
            return ResponseEntity.ok("Booking successful for seats: " + selectedSeats);
        } catch (BusNotFoundException | ScheduleNotFoundException |
                 SeatAlreadyBookedException | UserNotFoundException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Booking failed: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserReservations(@RequestParam String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        List<ReservationDto> reservations = reservationService.getUserReservations(user.get().getId());
        return ResponseEntity.ok(reservations);
    }

    @DeleteMapping("/cancel/{reservationId}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long reservationId) {
        try {
            reservationService.cancelReservation(reservationId);
            return ResponseEntity.ok("Reservation cancelled successfully");
        } catch (ReservationNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Cancellation failed: " + e.getMessage());
        }
    }
}

