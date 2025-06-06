package com.mahesh.busbookingbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusScheduleResponseDTO {
    private Long id;
    private BusResponseDTO busResponseDTO;
    private BusRouteResponseDTO routeResponseDTO;
    private LocalDate scheduleDate;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private int totalSeats;
    private int farePrice;
    private String createdAt;
    private String updatedAt;
    private String updatedBy;
    private String createdBy;
}
