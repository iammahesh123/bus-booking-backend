package com.mahesh.busbookingbackend.controller;

import com.mahesh.busbookingbackend.dtos.PassengerCreateDTO;
import com.mahesh.busbookingbackend.dtos.PassengerResponseDTO;
import com.mahesh.busbookingbackend.service.PassengerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("bus-passenger")
public class PassengerController {

    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @PostMapping
    private ResponseEntity<PassengerResponseDTO> createPassenger(@RequestBody PassengerCreateDTO passengerCreateDTO) {
        return ResponseEntity.ok(passengerService.createPassenger(passengerCreateDTO));
    }

    @PutMapping("/{passenger_id}")
    private ResponseEntity<PassengerResponseDTO> updatePassenger(@PathVariable("passenger_id") Long passengerId, @RequestBody PassengerCreateDTO passengerCreateDTO) {
        return ResponseEntity.ok(passengerService.updatePassenger(passengerId, passengerCreateDTO));
    }

    @GetMapping("/{passenger_id}")
    private ResponseEntity<PassengerResponseDTO> getPassenger(@PathVariable("passenger_id") Long passengerId) {
        return ResponseEntity.ok(passengerService.getPassenger(passengerId));
    }

    @GetMapping
    private ResponseEntity<List<PassengerResponseDTO>> getAllPassengers() {
        return ResponseEntity.ok(passengerService.getAllPassengers());
    }

    @DeleteMapping("/{passenger_id}")
    private ResponseEntity<HttpStatus> deletePassenger(@PathVariable("passenger_id") Long passengerId) {
        passengerService.deletePassenger(passengerId);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
