package com.mahesh.busbookingbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerResponseDTO {
    private Long id;
    private String passengerName;
    private int age;
    private String gender;
    private String seatNumber;
    private Long busId;
    private String createdAt;
    private String updatedAt;
    private String updatedBy;
    private String createdBy;
}
