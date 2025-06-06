package com.mahesh.busbookingbackend.service;

import com.mahesh.busbookingbackend.dtos.BusBookingCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusBookingDTO;

import java.util.List;

public interface BusBookingService {
    BusBookingDTO createBusBooking(BusBookingCreateDTO busBookingCreateDTO);
    BusBookingDTO updateBusBooking(Long bookingId, BusBookingCreateDTO busBookingCreateDTO);
    BusBookingDTO getBusBooking(Long bookingId);
    List<BusBookingDTO> getBusBookings();
    void deleteBusBooking(Long bookingId);

}
