package com.mahesh.busbookingbackend.mapper;

import com.mahesh.busbookingbackend.dtos.PassengerResponseDTO;
import com.mahesh.busbookingbackend.entity.PassengerEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PassengerMapper {
    public PassengerResponseDTO toDTO(PassengerEntity passenger, ModelMapper modelMapper) {
        return modelMapper.map(passenger, PassengerResponseDTO.class);
    }
}
