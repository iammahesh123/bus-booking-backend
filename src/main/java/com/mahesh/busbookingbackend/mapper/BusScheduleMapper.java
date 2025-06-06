package com.mahesh.busbookingbackend.mapper;

import com.mahesh.busbookingbackend.dtos.BusScheduleResponseDTO;
import com.mahesh.busbookingbackend.entity.BusScheduleEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class BusScheduleMapper {
    private final BusMapper busMapper;
    private final BusRouteMapper busRouteMapper;

    public BusScheduleMapper(BusMapper busMapper, BusRouteMapper busRouteMapper) {
        this.busMapper = busMapper;
        this.busRouteMapper = busRouteMapper;
    }

    public BusScheduleResponseDTO toDTO(BusScheduleEntity busScheduleEntity, ModelMapper modelMapper) {
        BusScheduleResponseDTO responseDTO = modelMapper.map(busScheduleEntity, BusScheduleResponseDTO.class);
        if(busScheduleEntity.getBusEntity() != null) {
            responseDTO.setBusResponseDTO(busMapper.toDTO(busScheduleEntity.getBusEntity(),modelMapper));
        }
        if(busScheduleEntity.getBusRoute() != null) {
            responseDTO.setRouteResponseDTO(busRouteMapper.toDTO(busScheduleEntity.getBusRoute(),modelMapper));
        }
        return responseDTO;
    }
}
