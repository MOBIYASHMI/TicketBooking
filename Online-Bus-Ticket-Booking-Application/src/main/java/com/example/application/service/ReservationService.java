package com.example.application.service;

import com.example.application.dto.ReservationDto;

import java.util.List;

public interface ReservationService {

    List<Integer> getBookedSeats(Long busId, Long scheduleId);

    void bookSeats(Long busId, Long scheduleId, List<Integer> seatNumbers, Long userId);


    List<ReservationDto> getUserReservations(Long userId);
    void cancelReservation(Long reservationId);
}
