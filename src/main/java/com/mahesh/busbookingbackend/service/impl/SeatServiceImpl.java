package com.mahesh.busbookingbackend.service.impl;

import com.mahesh.busbookingbackend.dtos.SeatCreateDTO;
import com.mahesh.busbookingbackend.dtos.SeatDTO;
import com.mahesh.busbookingbackend.entity.BusScheduleEntity;
import com.mahesh.busbookingbackend.entity.SeatEntity;
import com.mahesh.busbookingbackend.enums.SeatStatus;
import com.mahesh.busbookingbackend.enums.SeatType;
import com.mahesh.busbookingbackend.exception.ResourceNotFoundException;
import com.mahesh.busbookingbackend.mapper.SeatMapper;
import com.mahesh.busbookingbackend.repository.BusScheduleRepository;
import com.mahesh.busbookingbackend.repository.SeatRepository;
import com.mahesh.busbookingbackend.service.SeatService;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SeatServiceImpl implements SeatService {
    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;
    private final ModelMapper modelMapper;
    private final BusScheduleRepository busScheduleRepository;

    public SeatServiceImpl(SeatRepository seatRepository, SeatMapper seatMapper, ModelMapper modelMapper, BusScheduleRepository busScheduleRepository) {
        this.seatRepository = seatRepository;
        this.seatMapper = seatMapper;
        this.modelMapper = modelMapper;
        this.busScheduleRepository = busScheduleRepository;
    }

    @Override
    @Transactional
    public List<SeatDTO> generateSeats(long scheduleId) {
        // Fetch the schedule entity
        BusScheduleEntity busSchedule = busScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + scheduleId));

        List<SeatDTO> seatDTOs = new ArrayList<>();
        List<SeatEntity> seatEntities = new ArrayList<>();

        int rows = 10;
        int seatsPerRow = 4;

        for (int row = 1; row <= rows; row++) {
            double rowMultiplier = row <= 3 ? 1.0 : row <= 7 ? 0.9 : 0.8;

            for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                char seatLetter = (char) (64 + seatNum); // A, B, C, D
                String seatNumber = scheduleId + "-" + row + seatLetter; // Globally unique

                SeatType type = (seatNum == 1 || seatNum == 4) ? SeatType.WINDOW : SeatType.AISLE;
                int basePrice = getBasePrice(scheduleId);
                int price = calculatePrice(basePrice, rowMultiplier, type);

                SeatEntity seatEntity = new SeatEntity();
                seatEntity.setSeatNumber(seatNumber);
                seatEntity.setSeatType(type);
                seatEntity.setSeatStatus(SeatStatus.AVAILABLE);
                seatEntity.setSeatPrice(price);
                seatEntity.setBusSchedule(busSchedule);

                seatEntities.add(seatEntity); // Collect all seats
            }
        }

        // Persist all seats in batch
        List<SeatEntity> savedSeats = seatRepository.saveAll(seatEntities);

        // Map to DTOs
        for (SeatEntity seat : savedSeats) {
            seatDTOs.add(seatMapper.toDTO(seat, modelMapper));
        }

        return seatDTOs;
    }

    private int getBasePrice(long scheduleId) {
        return switch ((int) scheduleId) {
            case 1 -> 1800;
            case 2 -> 1200;
            case 3 -> 850;
            case 4 -> 450;
            case 5 -> 650;
            default -> 1750;
        };
    }

    private int calculatePrice(int basePrice, double rowMultiplier, SeatType type) {
        double price = basePrice * rowMultiplier;
        return (int) (type == SeatType.WINDOW ? price * 1.2 : price * 0.9);
    }

    @Override
    public SeatDTO createSeat(SeatCreateDTO seatCreateDTO) {
        SeatEntity seatEntity = new SeatEntity();
        BeanUtils.copyProperties(seatCreateDTO, seatEntity);
        SeatEntity savedSeat = seatRepository.save(seatEntity);
        return seatMapper.toDTO(savedSeat,modelMapper);
    }

    @Override
    public SeatDTO updateSeat(Long id, SeatCreateDTO seatCreateDTO) {
        SeatEntity existingSeat = seatRepository.findById(id).orElse(null);
        BeanUtils.copyProperties(seatCreateDTO, existingSeat);
        SeatEntity updatedSeat = seatRepository.save(existingSeat);
        return seatMapper.toDTO(updatedSeat,modelMapper);
    }

    @Override
    public SeatDTO getSeat(Long id) {
        SeatEntity existingSeat = seatRepository.findById(id).orElse(null);
        return seatMapper.toDTO(existingSeat,modelMapper);
    }

    @Override
    public List<SeatDTO> getAllSeats() {
        List<SeatEntity> seatEntities = seatRepository.findAll();
        return seatEntities.stream().map(seatEntity -> seatMapper.toDTO(seatEntity,modelMapper)).collect(Collectors.toList());
    }

    @Override
    public void deleteSeat(Long id) {
        SeatEntity existingSeat = seatRepository.findById(id).orElse(null);
        seatRepository.delete(existingSeat);
    }


    @Override
    public List<SeatDTO> viewSeats(long scheduleId) {
        BusScheduleEntity busSchedule = busScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + scheduleId));
        //log.info(busSchedule.toString()); //headache error
        List<SeatEntity> seatEntities = seatRepository.findByBusScheduleId(scheduleId);
        return seatEntities.stream()
                .map(seat -> seatMapper.toDTO(seat, modelMapper))
                .collect(Collectors.toList());
    }

    @Transactional
    @Retryable(value = {OptimisticLockingFailureException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public SeatDTO lockSeat(Long scheduleId, Long seatNumber) {
        SeatEntity seat = seatRepository.findSeatForBookingWithLock(scheduleId, seatNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));

        // Add explicit validation
        if (seat.getSeatStatus() == null) {
            throw new IllegalStateException("Seat status cannot be null");
        }

        if (seat.getSeatStatus() != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("Seat is already " + seat.getSeatStatus());
        }

        seat.setSeatStatus(SeatStatus.PENDING);
        // Consider adding flush to immediately detect any DB constraint violations
        seatRepository.saveAndFlush(seat);

        return seatMapper.toDTO(seat, modelMapper);
    }
}
