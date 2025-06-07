package com.mahesh.busbookingbackend.service.impl;

import com.mahesh.busbookingbackend.dtos.BusBookingCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusBookingDTO;
import com.mahesh.busbookingbackend.dtos.SeatDTO;
import com.mahesh.busbookingbackend.entity.BusBookingEntity;
import com.mahesh.busbookingbackend.entity.SeatEntity;
import com.mahesh.busbookingbackend.enums.BookingStatus;
import com.mahesh.busbookingbackend.enums.SeatStatus;
import com.mahesh.busbookingbackend.exception.ResourceNotFoundException;
import com.mahesh.busbookingbackend.mapper.BusBookingMapper;
import com.mahesh.busbookingbackend.repository.BusBookingRepository;
import com.mahesh.busbookingbackend.repository.SeatRepository;
import com.mahesh.busbookingbackend.service.BusBookingService;
import com.mahesh.busbookingbackend.service.SeatService;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BusBookingServiceImpl implements BusBookingService {
    private final BusBookingRepository busBookingRepository;
    private final BusBookingMapper busBookingMapper;
    private final ModelMapper modelMapper;
    private final SeatService seatService;
    private final SeatRepository seatRepository;

    private static final int BOOKING_EXPIRY_MINUTES = 15;

    public BusBookingServiceImpl(BusBookingRepository busBookingRepository, BusBookingMapper busBookingMapper, ModelMapper modelMapper, SeatService seatService, SeatRepository seatRepository) {
        this.busBookingRepository = busBookingRepository;
        this.busBookingMapper = busBookingMapper;
        this.modelMapper = modelMapper;
        this.seatService = seatService;
        this.seatRepository = seatRepository;
    }

    @Override
    @Transactional
    @Retryable(value = {OptimisticLockingFailureException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public BusBookingDTO createBusBooking(BusBookingCreateDTO busBookingCreateDTO) {
        BusBookingEntity busBookingEntity = new BusBookingEntity();
        BeanUtils.copyProperties(busBookingCreateDTO, busBookingEntity);
        busBookingEntity.setExpiryTime(LocalDateTime.now().plusMinutes(BOOKING_EXPIRY_MINUTES));
        BusBookingEntity savedBooking = busBookingRepository.save(busBookingEntity);
        Set<Long> seatNumbers = busBookingCreateDTO.getSeatIds();
        long scheduleId = busBookingCreateDTO.getBusScheduleId();
        double totalPrice = 0;
        for (Long seatNumber : seatNumbers) {
            SeatDTO seatDTO = seatService.lockSeat(scheduleId, seatNumber);
            SeatEntity seat = seatRepository.findById(seatDTO.getId()).orElseThrow();
            seat.setSeatStatus(SeatStatus.PENDING);
            seat.setBusBooking(busBookingEntity);
            busBookingEntity.getSeats().add(seat);

            totalPrice += seat.getSeatPrice();
        }
        savedBooking.setTotalPrice(totalPrice);
        savedBooking.setBookingStatus(BookingStatus.PENDING);
        busBookingRepository.save(busBookingEntity);
        return busBookingMapper.toDTO(savedBooking,modelMapper);
    }


    @Override
    @Transactional
    @Retryable(value = {OptimisticLockingFailureException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public BusBookingDTO updateBusBooking(Long bookingId, BusBookingCreateDTO busBookingCreateDTO) {
        // Get existing booking
        BusBookingEntity existingBooking = busBookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("booking id " + bookingId + " not found")
        );

        // Check if booking is expired (similar to confirmPayment)
        if (existingBooking.getExpiryTime().isBefore(LocalDateTime.now())) {
            releaseSeats(existingBooking);
            throw new ResourceNotFoundException("Booking has expired and cannot be updated");
        }

        // Release previously booked seats (similar to releaseSeats)
        existingBooking.getSeats().forEach(seat -> {
            seat.setSeatStatus(SeatStatus.AVAILABLE);
            seat.setBusBooking(null);
            seatRepository.save(seat);
        });
        existingBooking.getSeats().clear();

        // Process new seat selection (similar to createBusBooking)
        Set<Long> seatNumbers = busBookingCreateDTO.getSeatIds();
        long scheduleId = busBookingCreateDTO.getBusScheduleId();
        double totalPrice = 0;

        for (Long seatNumber : seatNumbers) {
            SeatDTO seatDTO = seatService.lockSeat(scheduleId, seatNumber);
            SeatEntity seat = seatRepository.findById(seatDTO.getId()).orElseThrow();
            seat.setSeatStatus(SeatStatus.PENDING);
            seat.setBusBooking(existingBooking);
            existingBooking.getSeats().add(seat);
            totalPrice += seat.getSeatPrice();
        }

        // Update booking properties (similar to original update)
        BeanUtils.copyProperties(busBookingCreateDTO, existingBooking);
        existingBooking.setTotalPrice(totalPrice);
        existingBooking.setExpiryTime(LocalDateTime.now().plusMinutes(BOOKING_EXPIRY_MINUTES));

        BusBookingEntity updatedBooking = busBookingRepository.save(existingBooking);
        return busBookingMapper.toDTO(updatedBooking, modelMapper);
    }

    @Override
    public BusBookingDTO getBusBooking(Long bookingId) {
        BusBookingEntity existingBooking = busBookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("booking id " + bookingId + " not found")
        );
        return busBookingMapper.toDTO(existingBooking,modelMapper);
    }

    @Override
    public List<BusBookingDTO> getBusBookings() {
        List<BusBookingEntity> bookings = busBookingRepository.findAll();
        return bookings.stream().map(booking -> busBookingMapper.toDTO(booking,modelMapper)).collect(Collectors.toList());
    }

    @Override
    public void deleteBusBooking(Long bookingId) {
        BusBookingEntity existingBooking = busBookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("booking id " + bookingId + " not found"));
        busBookingRepository.delete(existingBooking);
    }

    @Transactional
    public BusBookingDTO confirmPayment(Long bookingId) {
        BusBookingEntity booking = busBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (booking.getExpiryTime().isBefore(LocalDateTime.now())) {
            releaseSeats(booking);
            throw new ResourceNotFoundException("Booking has expired");
        }

        booking.getSeats().forEach(seat -> seat.setSeatStatus(SeatStatus.BOOKED));
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        BusBookingEntity updatedBooking = busBookingRepository.save(booking);
        return busBookingMapper.toDTO(updatedBooking, modelMapper);
    }

    @Transactional
    protected void releaseSeats(BusBookingEntity booking) {
        booking.getSeats().forEach(seat -> {
            seat.setSeatStatus(SeatStatus.AVAILABLE);
            seat.setBusBooking(null);
            seatRepository.save(seat);
        });
        booking.setBookingStatus(BookingStatus.EXPIRED);
        busBookingRepository.save(booking);
    }

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void checkExpiredBookings() {
        List<BusBookingEntity> expiredBookings = busBookingRepository
                .findByBookingStatusAndExpiryTimeBefore(BookingStatus.PENDING, LocalDateTime.now());
        expiredBookings.forEach(this::releaseSeats);
    }
}
