package com.example.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public class BusDto {

    private Long id;

    @NotBlank(message = "Bus name is required")
    @Size(max = 30,message = "Bus name should not exceed 30 characters")
    private String busName;

    @NotBlank(message = "Bus type is required")
    private String busType;

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    @NotNull(message = "Total seats are required")
    @Positive(message = "Total seats must be positive number")
    private Integer totalSeats;

    @Valid
    private List<ScheduleDto> schedules;

    public BusDto() {
    }

    public BusDto(Long id, String busName, String busType, String registrationNumber, Integer totalSeats, List<ScheduleDto> schedules) {
        this.id = id;
        this.busName = busName;
        this.busType = busType;
        this.registrationNumber = registrationNumber;
        this.totalSeats = totalSeats;
        this.schedules = schedules;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Bus name is required") @Size(max = 30, message = "Bus name should not exceed 30 characters") String getBusName() {
        return busName;
    }

    public void setBusName(@NotBlank(message = "Bus name is required") @Size(max = 30, message = "Bus name should not exceed 30 characters") String busName) {
        this.busName = busName;
    }

    public @NotBlank(message = "Bus type is required") String getBusType() {
        return busType;
    }

    public void setBusType(@NotBlank(message = "Bus type is required") String busType) {
        this.busType = busType;
    }

    public @NotBlank(message = "Registration number is required") String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(@NotBlank(message = "Registration number is required") String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public @NotNull(message = "Total seats are required") @Positive(message = "Total seats must be positive number") Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(@NotNull(message = "Total seats are required") @Positive(message = "Total seats must be positive number") Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public @Valid List<ScheduleDto> getSchedules() {
        return schedules;
    }

    public void setSchedules(@Valid List<ScheduleDto> schedules) {
        this.schedules = schedules;
    }
}
