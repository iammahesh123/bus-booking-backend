package com.mahesh.busbookingbackend.dtos;

import com.mahesh.busbookingbackend.enums.BusAmenities;
import com.mahesh.busbookingbackend.enums.BusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusCreateDTO {
    private String busName;
    private String busNumber;
    private BusType busType;
    private List<BusAmenities> busAmenities;
    private String operatorName;
    private String operatorNumber;
    private int totalSeats;
}

