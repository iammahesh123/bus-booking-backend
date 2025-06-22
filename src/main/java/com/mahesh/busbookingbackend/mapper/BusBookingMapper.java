package com.mahesh.busbookingbackend.mapper;

import com.mahesh.busbookingbackend.dtos.BusBookingDTO;
import com.mahesh.busbookingbackend.dtos.PassengerResponseDTO;
import com.mahesh.busbookingbackend.dtos.SeatDTO;
import com.mahesh.busbookingbackend.entity.BusBookingEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BusBookingMapper {
    public BusBookingDTO toDTO(BusBookingEntity busBooking, ModelMapper modelMapper) {
        BusBookingDTO dto = modelMapper.map(busBooking, BusBookingDTO.class);

        if(busBooking.getPassengers() != null) {
            List<PassengerResponseDTO> passengerDTOs = busBooking.getPassengers().stream()
                    .map(passenger -> modelMapper.map(passenger, PassengerResponseDTO.class))
                    .collect(Collectors.toList());
            dto.setPassengerResponseDTOS(passengerDTOs);
        }

        if(busBooking.getSeats() != null) {
            Set<SeatDTO> seatDTOs = busBooking.getSeats().stream()
                    .map(seat -> modelMapper.map(seat, SeatDTO.class))
                    .collect(Collectors.toSet());
            dto.setSeatDTOS(seatDTOs);
        }

        return dto;
    }
}