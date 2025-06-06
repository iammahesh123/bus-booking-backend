package com.mahesh.busbookingbackend.service.impl;

import com.mahesh.busbookingbackend.dtos.BusBookingCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusBookingDTO;
import com.mahesh.busbookingbackend.entity.BusBookingEntity;
import com.mahesh.busbookingbackend.exception.ResourceNotFoundException;
import com.mahesh.busbookingbackend.mapper.BusBookingMapper;
import com.mahesh.busbookingbackend.repository.BusBookingRepository;
import com.mahesh.busbookingbackend.service.BusBookingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusBookingServiceImpl implements BusBookingService {
    private final BusBookingRepository busBookingRepository;
    private final BusBookingMapper busBookingMapper;
    private final ModelMapper modelMapper;

    public BusBookingServiceImpl(BusBookingRepository busBookingRepository, BusBookingMapper busBookingMapper, ModelMapper modelMapper) {
        this.busBookingRepository = busBookingRepository;
        this.busBookingMapper = busBookingMapper;
        this.modelMapper = modelMapper;
    }

    @Override
    public BusBookingDTO createBusBooking(BusBookingCreateDTO busBookingCreateDTO) {
        BusBookingEntity busBookingEntity = new BusBookingEntity();
        BeanUtils.copyProperties(busBookingCreateDTO, busBookingEntity);
        BusBookingEntity savedBooking = busBookingRepository.save(busBookingEntity);
        return busBookingMapper.toDTO(savedBooking,modelMapper);
    }

    @Override
    public BusBookingDTO updateBusBooking(Long bookingId, BusBookingCreateDTO busBookingCreateDTO) {
        BusBookingEntity existingBooking = busBookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("booking id " + bookingId + " not found")
        );
        BeanUtils.copyProperties(busBookingCreateDTO, existingBooking);
        BusBookingEntity updatedBooking = busBookingRepository.save(existingBooking);
        return busBookingMapper.toDTO(updatedBooking,modelMapper);
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
}
