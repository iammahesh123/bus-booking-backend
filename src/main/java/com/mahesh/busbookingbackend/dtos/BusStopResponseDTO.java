package com.mahesh.busbookingbackend.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusStopResponseDTO {
    private Long id;
    private String stopName;

    @NotNull(message = "Arrival time cannot be null")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime arrivalTime;

    @NotNull(message = "Departure time cannot be null")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime departureTime;
    private int distance;
    private String createdAt;
    private String updatedAt;
    private String updatedBy;
    private String createdBy;
}
