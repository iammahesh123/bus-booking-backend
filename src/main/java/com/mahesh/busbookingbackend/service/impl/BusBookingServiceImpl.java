package com.mahesh.busbookingbackend.service.impl;

import com.mahesh.busbookingbackend.dtos.BusBookingCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusBookingDTO;
import com.mahesh.busbookingbackend.dtos.SeatDTO;
import com.mahesh.busbookingbackend.entity.*;
import com.mahesh.busbookingbackend.enums.BookingStatus;
import com.mahesh.busbookingbackend.enums.SeatStatus;
import com.mahesh.busbookingbackend.exception.ResourceNotFoundException;
import com.mahesh.busbookingbackend.mapper.BusBookingMapper;
import com.mahesh.busbookingbackend.repository.*;
import com.mahesh.busbookingbackend.service.BusBookingService;
import com.mahesh.busbookingbackend.service.EmailService;
import com.mahesh.busbookingbackend.service.SeatService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BusBookingServiceImpl implements BusBookingService {
    private final BusBookingRepository busBookingRepository;
    private final BusBookingMapper busBookingMapper;
    private final ModelMapper modelMapper;
    private final SeatService seatService;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private static final int BOOKING_EXPIRY_MINUTES = 15;
    private final PassengerRepository passengerRepository;
    private final BusScheduleRepository busScheduleRepository;

    public BusBookingServiceImpl(BusBookingRepository busBookingRepository, BusBookingMapper busBookingMapper, ModelMapper modelMapper, SeatService seatService, SeatRepository seatRepository, UserRepository userRepository, EmailService emailService, PassengerRepository passengerRepository, BusScheduleRepository busScheduleRepository) {
        this.busBookingRepository = busBookingRepository;
        this.busBookingMapper = busBookingMapper;
        this.modelMapper = modelMapper;
        this.seatService = seatService;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passengerRepository = passengerRepository;
        this.busScheduleRepository = busScheduleRepository;
    }

   @Override
    @Transactional
    @Retryable(value = {OptimisticLockingFailureException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public BusBookingDTO createBusBooking(BusBookingCreateDTO busBookingCreateDTO) {
        BusBookingEntity busBookingEntity = new BusBookingEntity();
        BeanUtils.copyProperties(busBookingCreateDTO, busBookingEntity);

        long scheduleId = busBookingCreateDTO.getBusScheduleId();
        BusScheduleEntity busSchedule = busScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));
        busBookingEntity.setBusSchedule(busSchedule);

        busBookingEntity.setExpiryTime(LocalDateTime.now().plusMinutes(BOOKING_EXPIRY_MINUTES));
        BusBookingEntity savedBooking = busBookingRepository.save(busBookingEntity);
        Set<Long> seatNumbers = busBookingCreateDTO.getSeatIds();
        double totalPrice = 0;
        for (Long seatNumber : seatNumbers) {
            SeatDTO seatDTO = seatService.lockSeat(scheduleId, seatNumber);
            SeatEntity seat = seatRepository.findById(seatDTO.getId()).orElseThrow();
            seat.setSeatStatus(SeatStatus.PENDING);
            seat.setBusBooking(busBookingEntity);
            busBookingEntity.getSeats().add(seat);

            totalPrice += seat.getSeatPrice();
        }
        List<Long> passengerIds = busBookingCreateDTO.getPassengerIds();
        for( Long passengerId : passengerIds) {
            PassengerEntity passenger = passengerRepository.findById(passengerId).orElseThrow(
                    () -> new ResourceNotFoundException("Passenger with id " + passengerId + " not found")
            );
            busBookingEntity.getPassengers().add(passenger);
            passenger.setBusBooking(busBookingEntity);
            passengerRepository.save(passenger);
        }
        savedBooking.setTotalPrice(totalPrice);
        savedBooking.setBookingStatus(BookingStatus.PENDING);
        savedBooking.setBusName(busSchedule.getBusEntity().getBusName());
        savedBooking.setBusNumber(busSchedule.getBusEntity().getBusNumber());
        savedBooking.setSourceCity(busSchedule.getBusRoute().getSourceCity());
        savedBooking.setDestinationCity(busSchedule.getBusRoute().getDestinationCity());
        savedBooking.setDepartureTime(busSchedule.getDepartureTime().toString());
        savedBooking.setArrivalTime(busSchedule.getArrivalTime().toString());

        busBookingRepository.save(busBookingEntity);
        return busBookingMapper.toDTO(savedBooking,modelMapper);
    }


    @Override
    @Transactional
    @Retryable(value = {OptimisticLockingFailureException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public BusBookingDTO updateBusBooking(Long bookingId, BusBookingCreateDTO busBookingCreateDTO) {
        BusBookingEntity existingBooking = busBookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("booking id " + bookingId + " not found")
        );

        if (existingBooking.getExpiryTime().isBefore(LocalDateTime.now())) {
            releaseSeats(existingBooking);
            throw new ResourceNotFoundException("Booking has expired and cannot be updated");
        }
        long scheduleId = busBookingCreateDTO.getBusScheduleId();
        BusScheduleEntity busSchedule = busScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));
        existingBooking.setBusSchedule(busSchedule);

        existingBooking.getSeats().forEach(seat -> {
            seat.setSeatStatus(SeatStatus.AVAILABLE);
            seat.setBusBooking(null);
            seatRepository.save(seat);
        });
        existingBooking.getSeats().clear();

        Set<Long> seatNumbers = busBookingCreateDTO.getSeatIds();
        double totalPrice = 0;

        for (Long seatNumber : seatNumbers) {
            SeatDTO seatDTO = seatService.lockSeat(scheduleId, seatNumber);
            SeatEntity seat = seatRepository.findById(seatDTO.getId()).orElseThrow();
            seat.setSeatStatus(SeatStatus.PENDING);
            seat.setBusBooking(existingBooking);
            existingBooking.getSeats().add(seat);
            totalPrice += seat.getSeatPrice();
        }

        BeanUtils.copyProperties(busBookingCreateDTO, existingBooking);
        existingBooking.setTotalPrice(totalPrice);
        existingBooking.setExpiryTime(LocalDateTime.now().plusMinutes(BOOKING_EXPIRY_MINUTES));
        existingBooking.setBusName(busSchedule.getBusEntity().getBusName());
        existingBooking.setBusNumber(busSchedule.getBusEntity().getBusNumber());
        existingBooking.setSourceCity(busSchedule.getBusRoute().getSourceCity());
        existingBooking.setDestinationCity(busSchedule.getBusRoute().getDestinationCity());
        existingBooking.setDepartureTime(busSchedule.getDepartureTime().toString());
        existingBooking.setArrivalTime(busSchedule.getArrivalTime().toString());
        BusBookingEntity updatedBooking = busBookingRepository.save(existingBooking);
        return busBookingMapper.toDTO(updatedBooking, modelMapper);
    }

    @Override
    public BusBookingDTO getBusBooking(Long bookingId) {
        BusBookingEntity existingBooking = busBookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("booking id " + bookingId + " not found"));
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
        sendBookingConfirmationEmail(updatedBooking);

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

    private void sendBookingConfirmationEmail(BusBookingEntity booking) {
        UserEntity user = userRepository.findByEmail(booking.getUserId());
        if (user == null) {
            log.warn("User with email {} not found. Cannot send booking confirmation.", booking.getUserId());
            return;
        }

        String subject = "Your Blue Bus Booking is Confirmed! Booking ID: " + booking.getBookingNumber();
        String seatNumbers = booking.getSeats().stream()
                .map(SeatEntity::getSeatNumber)
                .collect(Collectors.joining(", "));

        String passengerNames = booking.getPassengers().stream()
                .map(PassengerEntity::getPassengerName)
                .collect(Collectors.joining(", "));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        String formattedDate = booking.getBusSchedule().getScheduleDate().format(dateFormatter);
        String formattedDeparture = booking.getBusSchedule().getDepartureTime().format(timeFormatter);
        String formattedArrival = booking.getBusSchedule().getArrivalTime().format(timeFormatter);
        String emailBody = String.format("""
            <html>
                <body style="font-family: Arial, sans-serif; color: #333;">
                    <h2>Hello %s,</h2>
                    <p>Thank you for booking with Blue Bus. Your booking is confirmed!</p>
                    <h3 style="color: #0056b3;">Booking Details:</h3>
                    <table border="1" cellpadding="10" style="border-collapse: collapse; width: 100%%;">
                        <tr><td style="width: 30%%;"><strong>Booking ID:</strong></td><td>%s</td></tr>
                        <tr><td><strong>Bus:</strong></td><td>%s</td></tr>
                        <tr><td><strong>Route:</strong></td><td>%s to %s</td></tr>
                        <tr><td><strong>Date of Journey:</strong></td><td>%s</td></tr>
                        <tr><td><strong>Departure Time:</strong></td><td>%s</td></tr>
                        <tr><td><strong>Arrival Time:</strong></td><td>%s</td></tr>
                        <tr><td><strong>Seat(s):</strong></td><td>%s</td></tr>
                        <tr><td><strong>Total Fare:</strong></td><td>₹%.2f</td></tr>
                        <tr><td><strong>Passengers:</strong></td><td>%s</td></tr>
                    </table>
                    <p>We wish you a safe and pleasant journey!</p>
                    <p>Best Regards,<br/>The Blue Bus Team</p>
                </body>
            </html>
            """,
                user.getFullName(),
                booking.getId(),
                booking.getBusSchedule().getBusEntity().getBusName(),
                booking.getBusSchedule().getBusRoute().getSourceCity(),
                booking.getBusSchedule().getBusRoute().getDestinationCity(),
                formattedDate,
                formattedDeparture,
                formattedArrival,
                seatNumbers,
                booking.getTotalPrice(),
                passengerNames
        );
        emailService.sendEmail(user.getEmail(), subject, emailBody);
        log.info("Booking confirmation email sent to {} for booking ID {}", user.getEmail(), booking.getBookingNumber());
    }
}