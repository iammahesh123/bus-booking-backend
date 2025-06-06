package com.mahesh.busbookingbackend.controller;

import com.mahesh.busbookingbackend.dtos.BusBookingCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusBookingDTO;
import com.mahesh.busbookingbackend.service.BusBookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bus-booking")
public class BusBookingController {

    private final BusBookingService busBookingService;

    public BusBookingController(BusBookingService busBookingService) {
        this.busBookingService = busBookingService;
    }

    @PostMapping
    private ResponseEntity<BusBookingDTO> createBusBooking(@RequestBody BusBookingCreateDTO busBookingCreateDTO) {
        return ResponseEntity.ok(busBookingService.createBusBooking(busBookingCreateDTO));
    }

    @PutMapping("/{booking_id}")
    private ResponseEntity<BusBookingDTO> updateBooking(@PathVariable("booking_id") Long bookingId, @RequestBody BusBookingCreateDTO busBookingCreateDTO) {
        return ResponseEntity.ok(busBookingService.updateBusBooking(bookingId, busBookingCreateDTO));
    }

    @GetMapping("/{booking_id}")
    private ResponseEntity<BusBookingDTO> getBooking(@PathVariable("booking_id") Long bookingId) {
        return ResponseEntity.ok(busBookingService.getBusBooking(bookingId));
    }

    @GetMapping
    private ResponseEntity<List<BusBookingDTO>> getAllBookings() {
        return ResponseEntity.ok(busBookingService.getBusBookings());
    }

    @DeleteMapping("/{booking_id}")
    private ResponseEntity<HttpStatus> deleteBooking(@PathVariable("booking_id") Long bookingId) {
        busBookingService.deleteBusBooking(bookingId);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
