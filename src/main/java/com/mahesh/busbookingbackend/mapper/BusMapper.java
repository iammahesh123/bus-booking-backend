package com.mahesh.busbookingbackend.mapper;

import com.mahesh.busbookingbackend.dtos.BusResponseDTO;
import com.mahesh.busbookingbackend.entity.BusEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class BusMapper {
    public BusResponseDTO toDTO(BusEntity busEntity, ModelMapper modelMapper) {
        BusResponseDTO responseDTO =  modelMapper.map(busEntity, BusResponseDTO.class);
        if(busEntity.getBusAmenities() != null || !responseDTO.getBusAmenities().isEmpty()) {
            responseDTO.setBusAmenities(busEntity.getBusAmenities());
        }
        return responseDTO;
    }
}
