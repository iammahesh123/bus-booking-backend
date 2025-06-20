package com.mahesh.busbookingbackend.dtos;

import com.mahesh.busbookingbackend.enums.ScheduleDuration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusScheduleCreateDTO {
    private long busId;
    private long routeId;
    private LocalDate scheduleDate;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private int totalSeats;
    private int farePrice;
    private ScheduleDuration automationDuration;
    private boolean isMasterRecord;
}
