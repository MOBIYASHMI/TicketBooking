package com.example.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ScheduleDto {

    private Long id;

    @Null(message = "Bus ID should be null when creating a new bus")
    private Long busId;

    @NotBlank(message = "Source location is required")
    private String source;

    @NotBlank(message = "Destination location is required")
    private String destination;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Future(message = "Scheduled date should be in future")
    private LocalDate scheduledDate;

    @DateTimeFormat(pattern = "HH:mm")
    @NotNull(message = "Arrival time is required")
    private String arrivalTime;

    @DateTimeFormat(pattern = "HH:mm")
    @NotNull(message = "Departure time is required")
    private String departureTime;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive number")
    private Double price;

    @NotNull(message = "Available seats is required")
    @Positive(message = "Available seats must be positive number")
    private Integer availableSeats;

    @JsonIgnoreProperties("schedules")
    private BusDto bus;

    public ScheduleDto() {
    }

    public ScheduleDto(Long id, Long busId, String source, String destination, LocalDate scheduledDate, String arrivalTime, String departureTime, Double price, Integer availableSeats) {
        this.id = id;
        this.busId = busId;
        this.source = source;
        this.destination = destination;
        this.scheduledDate = scheduledDate;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.price = price;
        this.availableSeats = availableSeats;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @Positive(message = "Bus Id should be positive") Long getBusId() {
        return busId;
    }

    public void setBusId(@Positive(message = "Bus Id should be positive") Long busId) {
        this.busId = busId;
    }

    public @NotBlank(message = "Source location is required") String getSource() {
        return source;
    }

    public void setSource(@NotBlank(message = "Source location is required") String source) {
        this.source = source;
    }

    public @NotBlank(message = "Destination location is required") String getDestination() {
        return destination;
    }

    public void setDestination(@NotBlank(message = "Destination location is required") String destination) {
        this.destination = destination;
    }

    public @Future(message = "Scheduled date should be in future") LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(@Future(message = "Scheduled date should be in future") LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public @NotNull(message = "Arrival time is required") String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(@NotNull(message = "Arrival time is required") String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public @NotNull(message = "Departure time is required") String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(@NotNull(message = "Departure time is required") String departureTime) {
        this.departureTime = departureTime;
    }

    public @NotNull(message = "Price is required") @Positive(message = "Price must be positive number") Double getPrice() {
        return price;
    }

    public void setPrice(@NotNull(message = "Price is required") @Positive(message = "Price must be positive number") Double price) {
        this.price = price;
    }

    public @NotNull(message = "Available seats is required") @Positive(message = "Available seats must be positive number") Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(@NotNull(message = "Available seats is required") @Positive(message = "Available seats must be positive number") Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public BusDto getBus() {
        return bus;
    }

    public void setBus(BusDto bus) {
        this.bus = bus;
    }
}
