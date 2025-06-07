package com.mahesh.busbookingbackend.mapper;

import com.mahesh.busbookingbackend.dtos.BusBookingDTO;
import com.mahesh.busbookingbackend.entity.BusBookingEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class BusBookingMapper {
    public BusBookingDTO toDTO(BusBookingEntity busBooking, ModelMapper modelMapper) {
        BusBookingDTO dto = modelMapper.map(busBooking, BusBookingDTO.class);
        if(busBooking.getPassengers() != null) {
            dto.setPassengerCreateDTOS(null);
        }
        if(busBooking.getSeats() != null) {
            dto.setSeatDTOS(null);
        }
        return dto;
    }
}
