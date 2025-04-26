package com.example.application.service.impl;

import com.example.application.dto.ReservationDto;
import com.example.application.entity.Bus;
import com.example.application.entity.Reservation;
import com.example.application.entity.Schedule;
import com.example.application.entity.User;
import com.example.application.exceptions.*;
import com.example.application.repository.BusRepository;
import com.example.application.repository.ReservationRepository;
import com.example.application.repository.ScheduleRepository;
import com.example.application.repository.UserRepository; // Assuming you have a UserRepository
import com.example.application.service.ReservationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository; // Inject UserRepository

    @Transactional
    @Override
    public void bookSeats(Long busId, Long scheduleId, List<Integer> seatNumbers, Long userId) {

        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException("Bus not found with ID: " + busId));

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with ID: " + scheduleId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        List<Reservation> existingReservations = reservationRepository.findByBusAndScheduleAndSeatNumbersIn(bus, schedule, seatNumbers);
        if (!existingReservations.isEmpty()) {
            List<Integer> alreadyBooked = existingReservations.stream()
                    .flatMap(r -> r.getSeatNumbers().stream())
                    .filter(seatNumbers::contains)
                    .distinct()
                    .collect(Collectors.toList());
            if (!alreadyBooked.isEmpty()) {
                throw new SeatAlreadyBookedException("Seats already booked: " + alreadyBooked);
            }
        }

        Reservation reservation = new Reservation();
        reservation.setBus(bus);
        reservation.setSchedule(schedule);
        reservation.setUser(user);
        reservation.setSeatNumbers(seatNumbers);
        reservation.setBookingDateTime(LocalDateTime.now());

        reservationRepository.save(reservation);

        schedule.setAvailableSeats(schedule.getAvailableSeats() - seatNumbers.size());
        scheduleRepository.save(schedule);
    }

    @Override
    public List<Integer> getBookedSeats(Long busId, Long scheduleId) throws BusNotFoundException, ScheduleNotFoundException {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException("Bus not found with ID: " + busId));

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with ID: " + scheduleId));

        List<Reservation> existingReservations = reservationRepository.findByBusAndSchedule(bus, schedule);
        return existingReservations.stream()
                .flatMap(reservation -> reservation.getSeatNumbers().stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDto> getUserReservations(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        List<Reservation> reservations = reservationRepository.findByUser(user);
        return reservations.stream()
                .map(reservation -> {
                    ReservationDto dto = new ReservationDto();
                    BeanUtils.copyProperties(reservation, dto);
                    dto.setBusId(reservation.getBus().getId());
                    dto.setScheduleId(reservation.getSchedule().getId());
                    dto.setUserId(reservation.getUser().getId());
                    Optional.ofNullable(reservation.getBus()).ifPresent(bus -> dto.setBusName(bus.getBusName()));
                    Optional.ofNullable(reservation.getSchedule()).ifPresent(schedule ->
                            dto.setScheduleDetails(schedule.getSource() + " to " + schedule.getDestination() + " on " + schedule.getScheduledDate()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void cancelReservation(Long reservationId){
        Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);
        if (reservationOptional.isEmpty()) {
            throw new ReservationNotFoundException("Reservation not found with ID: " + reservationId);
        }
        Reservation reservation = reservationOptional.get();
        Schedule schedule = reservation.getSchedule();
        List<Integer> cancelledSeats = reservation.getSeatNumbers();

        reservationRepository.deleteById(reservationId);

        schedule.setAvailableSeats(schedule.getAvailableSeats() + cancelledSeats.size());
        scheduleRepository.save(schedule);
    }




}
