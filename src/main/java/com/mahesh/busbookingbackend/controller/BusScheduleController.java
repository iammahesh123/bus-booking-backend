package com.mahesh.busbookingbackend.controller;

import com.mahesh.busbookingbackend.dtos.BusScheduleCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusScheduleResponseDTO;
import com.mahesh.busbookingbackend.service.BusScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/bus-schedule")
@Tag(name = "Bus Schedule Management", description = "APIs for managing bus Schedule, including creation, retrieval, update, and deletion of route information")
public class BusScheduleController {

    private final BusScheduleService busScheduleService;

    public BusScheduleController(BusScheduleService busScheduleService) {
        this.busScheduleService = busScheduleService;
    }

    @PostMapping
    private ResponseEntity<BusScheduleResponseDTO> createBusSchedule(@RequestBody BusScheduleCreateDTO busScheduleCreateDTO) {
        return ResponseEntity.ok(busScheduleService.createSchedule(busScheduleCreateDTO));
    }

    @PutMapping("/{bus_schedule_id}")
    private ResponseEntity<BusScheduleResponseDTO> updateSchedule(@PathVariable("bus_schedule_id") Long scheduleId, @RequestBody BusScheduleCreateDTO busScheduleCreateDTO) {
        return ResponseEntity.ok(busScheduleService.updateSchedule(scheduleId, busScheduleCreateDTO));
    }

    @GetMapping("/{bus_schedule_id}")
    private ResponseEntity<BusScheduleResponseDTO> getSchedule(@PathVariable("bus_schedule_id") Long scheduleId) {
        return ResponseEntity.ok(busScheduleService.getSchedule(scheduleId));
    }

    @GetMapping
    private ResponseEntity<List<BusScheduleResponseDTO>> getAllSchedules() {
        return ResponseEntity.ok(busScheduleService.getSchedules());
    }

    @DeleteMapping("/{bus_schedule_id}")
    private ResponseEntity<HttpStatus> deleteSchedule(@PathVariable("bus_schedule_id") Long scheduleId) {
        busScheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/fetch-schedules")
    public ResponseEntity<List<BusScheduleResponseDTO>> getSchedules(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(busScheduleService.getSchedules(source, destination, date));
    }

}
