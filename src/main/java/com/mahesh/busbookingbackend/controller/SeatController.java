package com.mahesh.busbookingbackend.controller;

import com.mahesh.busbookingbackend.dtos.SeatCreateDTO;
import com.mahesh.busbookingbackend.dtos.SeatDTO;
import com.mahesh.busbookingbackend.service.SeatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bus-seats")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/generate-seats/{schedule_id}")
    public ResponseEntity<List<SeatDTO>> generateSeatsForSchedule(
            @PathVariable("schedule_id") Long scheduleId) {
        List<SeatDTO> seats = seatService.generateSeats(scheduleId);
        return ResponseEntity.ok(seats);
    }

    @GetMapping("/view-seats/{schedule_id}")
    public ResponseEntity<List<SeatDTO>> viewSeatsForSchedule(
            @PathVariable("schedule_id") Long scheduleId) {
        List<SeatDTO> seats = seatService.viewSeats(scheduleId);
        return ResponseEntity.ok(seats);
    }
//
//    @PostMapping
//    private ResponseEntity<SeatDTO> createSeat(@RequestBody SeatCreateDTO seatCreateDTO) {
//        return ResponseEntity.ok(seatService.createSeat(seatCreateDTO));
//    }

    @PutMapping("/{seat_id}")
    private ResponseEntity<SeatDTO> updateSeat(@PathVariable("seat_id") Long seatId, @RequestBody SeatCreateDTO seatCreateDTO) {
        return ResponseEntity.ok(seatService.updateSeat(seatId,seatCreateDTO));
    }

    @GetMapping("/{seat_id}")
    private ResponseEntity<SeatDTO> getSeat(@PathVariable("seat_id") Long seatId) {
        return ResponseEntity.ok(seatService.getSeat(seatId));
    }

    @GetMapping
    private ResponseEntity<List<SeatDTO>> getAllSeats() {
        return ResponseEntity.ok(seatService.getAllSeats());
    }

    @DeleteMapping("/{seat_id}")
    private ResponseEntity<HttpStatus> deleteSeat(@PathVariable("seat_id") Long seatId) {
        seatService.deleteSeat(seatId);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
