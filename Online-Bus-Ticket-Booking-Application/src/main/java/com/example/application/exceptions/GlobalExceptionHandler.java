package com.example.application.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public String handleUsernameNotFoundException(UsernameNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", "Session expired or user not found. Please login again.");
        return "redirect:/auth/loginForm";
    }

    @ExceptionHandler(BusAlreadyExistsException.class)
    public String handleBusAlreadyExistsException(BusAlreadyExistsException ex, Model model) {
        model.addAttribute("errorMessage", "Bus already exists");
        return "redirect:/admin/dashboard";
    }

    @ExceptionHandler(BusNotFoundException.class)
    public String handleBusNotFoundException(BusNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", "Bus not found");
        return "redirect:/dashboard";
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public String handleReservationNotFoundException(ReservationNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", "Reservation not found");
        return "redirect:/dashboard";
    }

    @ExceptionHandler(ScheduleNotFoundException.class)
    public String handleScheduleNotFoundException(ScheduleNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", "Schedule not found");
        return "redirect:/dashboard";
    }

    @ExceptionHandler(SeatAlreadyBookedException.class)
    public String handleSeatAlreadyBookedException(SeatAlreadyBookedException ex, Model model) {
        model.addAttribute("errorMessage", "Seat is already booked");
        return "redirect:/dashboard";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", "User not found");
        return "redirect:/dashboard";
    }

}
