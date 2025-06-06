package com.mahesh.busbookingbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusRouteCreateDTO {
    private String sourceCity;
    private String destinationCity;
    private int totalDistance;
    private String totalDuration;
    private List<Long> stopIds;
}
