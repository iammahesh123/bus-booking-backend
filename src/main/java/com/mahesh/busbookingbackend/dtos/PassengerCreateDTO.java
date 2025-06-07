package com.mahesh.busbookingbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerCreateDTO {
    private String passengerName;
    private int age;
    private String gender;
    private String seatNumber;
    private Long busId;
}
