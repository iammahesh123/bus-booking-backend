package com.mahesh.busbookingbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusRouteResponseDTO {
    private Long id;
    private String sourceCity;
    private String destinationCity;
    private int totalDistance;
    private String totalDuration;
    private List<BusStopResponseDTO> stops;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String updatedBy;
}
