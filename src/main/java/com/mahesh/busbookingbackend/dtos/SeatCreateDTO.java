package com.mahesh.busbookingbackend.dtos;

import com.mahesh.busbookingbackend.enums.SeatStatus;
import com.mahesh.busbookingbackend.enums.SeatType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatCreateDTO {
    private String seatNumber;
    private SeatType seatType;
    private SeatStatus seatStatus;
    private int seatPrice;
    private long scheduleId;
}
