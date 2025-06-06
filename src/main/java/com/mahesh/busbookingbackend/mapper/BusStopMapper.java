package com.mahesh.busbookingbackend.mapper;

import com.mahesh.busbookingbackend.dtos.BusStopResponseDTO;
import com.mahesh.busbookingbackend.entity.BusStops;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class BusStopMapper {
    public BusStopResponseDTO toDTO(BusStops stops, ModelMapper modelMapper) {
        BusStopResponseDTO dto = modelMapper.map(stops, BusStopResponseDTO.class);
        return dto;
    }
}
