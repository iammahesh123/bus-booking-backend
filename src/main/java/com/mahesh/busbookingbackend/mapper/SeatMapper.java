package com.mahesh.busbookingbackend.mapper;

import com.mahesh.busbookingbackend.dtos.SeatDTO;
import com.mahesh.busbookingbackend.entity.SeatEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {
    public SeatDTO toDTO(SeatEntity seatEntity, ModelMapper modelMapper) {
        SeatDTO seatDTO =  modelMapper.map(seatEntity, SeatDTO.class);
        if(seatEntity.getBusSchedule() != null) {
            seatDTO.setScheduleId(seatEntity.getBusSchedule().getId());
        }
        return seatDTO;
    }
}
