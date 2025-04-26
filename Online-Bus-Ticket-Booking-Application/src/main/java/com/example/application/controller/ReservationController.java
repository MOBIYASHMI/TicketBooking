package com.example.application.controller;

import com.example.application.dto.BusDto;
import com.example.application.dto.ReservationDto;
import com.example.application.dto.ScheduleDto;
import com.example.application.entity.User;
import com.example.application.exceptions.*;
import com.example.application.repository.UserRepository;
import com.example.application.service.BusService;
import com.example.application.service.ReservationService;
import com.example.application.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    private BusService busService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/buslist")
    public String getBusList(@Valid @RequestParam String source,
                             @Valid @RequestParam String destination,
                             @Valid @RequestParam String date,
                             Model model) {
        try {
            if (LocalDate.parse(date).isBefore(LocalDate.now())) {
                model.addAttribute("errorMessage", "Departure date cannot be in the past.");
                return "error";
            }
            LocalDate departureDate = LocalDate.parse(date);
            List<ScheduleDto> busSchedules = scheduleService.findAvailableSchedules(source, destination, departureDate);
            model.addAttribute("source", source);
            model.addAttribute("destination", destination);
            model.addAttribute("date", departureDate);
            model.addAttribute("busSchedules", busSchedules);
            return "userBusList";
        } catch (ScheduleNotFoundException e) {
            model.addAttribute("errorMessage", "Error fetching bus list: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/seats")
    public String showSeatSelection(@RequestParam Long busId,
                                    @RequestParam Long scheduleId,
                                    @RequestParam String source,
                                    @RequestParam String destination,
                                    @RequestParam String date,
                                    Model model) {
        try {
            BusDto bus = busService.getByBusId(busId);
            ScheduleDto schedule = scheduleService.getScheduleById(scheduleId);
            List<Integer> bookedSeats = reservationService.getBookedSeats(busId, scheduleId);
            List<Integer> availableSeats = IntStream.rangeClosed(1, bus.getTotalSeats())
                    .boxed()
                    .filter(seatNumber -> !bookedSeats.contains(seatNumber))
                    .collect(Collectors.toList());

            List<Integer> allSeats = IntStream.rangeClosed(1, bus.getTotalSeats()).boxed().collect(Collectors.toList());

            model.addAttribute("bus", bus);
            model.addAttribute("schedule", schedule);
            model.addAttribute("availableSeats", availableSeats);
            model.addAttribute("allSeats", allSeats);
            model.addAttribute("source", source);
            model.addAttribute("destination", destination);
            model.addAttribute("date", date);
            return "seatSelection";
        } catch (BusNotFoundException | ScheduleNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/confirm")
    public String confirmBooking(@RequestParam Long busId,
                                 @RequestParam Long scheduleId,
                                 @RequestParam String selectedSeats,
                                 @RequestParam String source,
                                 @RequestParam String destination,
                                 @RequestParam String date,
                                 RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<User> user = userRepository.findByUsername(currentUsername);
        if (user.isEmpty() || user.get().getId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found. Please log in again.");
            return "redirect:/auth/login";
        }
        Long userId = user.get().getId();

        List<Integer> seatsToBook = Arrays.stream(selectedSeats.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        try {
            reservationService.bookSeats(busId, scheduleId, seatsToBook, userId);
            redirectAttributes.addFlashAttribute("message", "Booking successful for seats: " + selectedSeats);
            return "redirect:/reservation/reservations"; // Redirect to user's reservations
        } catch (BusNotFoundException | ScheduleNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/reservation/seats?busId=" + busId + "&scheduleId=" + scheduleId +
                    "&source=" + source + "&destination=" + destination + "&date=" + date;
        } catch(SeatAlreadyBookedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/reservation/seats?busId=" + busId + "&scheduleId=" + scheduleId +
                    "&source=" + source + "&destination=" + destination + "&date=" + date;
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found. Please log in again.");
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/reservations")
    public String viewUserReservations(Model model, Authentication authentication) {
        String username = authentication.getName();
        Optional<User> user = userRepository.findByUsername(username);
        if (user==null || user.get().getId() == null) {
            model.addAttribute("errorMessage", "User not found.");
            return "error";
        }
        Long userId = user.get().getId();

        List<ReservationDto> reservations = reservationService.getUserReservations(userId);
        model.addAttribute("reservations", reservations);
        return "userReservations"; // Create this HTML page
    }

    @PostMapping("/cancel")
    public String cancelReservation(@RequestParam Long reservationId, RedirectAttributes redirectAttributes) {
        try {
            reservationService.cancelReservation(reservationId);
            redirectAttributes.addFlashAttribute("message", "Reservation cancelled successfully.");
        } catch (ReservationNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/reservation/reservations";
    }

}
