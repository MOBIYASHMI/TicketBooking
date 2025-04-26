package com.example.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationDto {

    private Long id;
    private Long busId;
    private Long scheduleId;
    private Long userId;
    private List<Integer> seatNumbers;
    private LocalDateTime bookingDateTime;
    private String busName; // Optional: For display purposes
    private String scheduleDetails; // Optional: For display purposes

    // Constructors
    public ReservationDto() {
    }

    public ReservationDto(Long id, Long busId, Long scheduleId, Long userId, List<Integer> seatNumbers, LocalDateTime bookingDateTime, String busName, String scheduleDetails) {
        this.id = id;
        this.busId = busId;
        this.scheduleId = scheduleId;
        this.userId = userId;
        this.seatNumbers = seatNumbers;
        this.bookingDateTime = bookingDateTime;
        this.busName = busName;
        this.scheduleDetails = scheduleDetails;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public List<Integer> getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(List<Integer> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    public LocalDateTime getBookingDateTime() {
        return bookingDateTime;
    }

    public void setBookingDateTime(LocalDateTime bookingDateTime) {
        this.bookingDateTime = bookingDateTime;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getScheduleDetails() {
        return scheduleDetails;
    }

    public void setScheduleDetails(String scheduleDetails) {
        this.scheduleDetails = scheduleDetails;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
