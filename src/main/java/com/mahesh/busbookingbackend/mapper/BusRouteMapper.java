package com.mahesh.busbookingbackend.mapper;

import com.mahesh.busbookingbackend.dtos.BusRouteResponseDTO;
import com.mahesh.busbookingbackend.entity.BusRoute;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class BusRouteMapper {
    public BusRouteResponseDTO toDTO(BusRoute busRoute, ModelMapper modelMapper) {
        return modelMapper.map(busRoute, BusRouteResponseDTO.class);
    }
}
