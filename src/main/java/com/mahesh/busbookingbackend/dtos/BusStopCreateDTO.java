package com.mahesh.busbookingbackend.dtos;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusStopCreateDTO {
    private String stopName;
    @DateTimeFormat(pattern = "HH:mm")
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Time must be in HH:mm format")
    private LocalTime arrivalTime;

    @DateTimeFormat(pattern = "HH:mm")
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Time must be in HH:mm format")
    private LocalTime departureTime;
    private int distance;
}
