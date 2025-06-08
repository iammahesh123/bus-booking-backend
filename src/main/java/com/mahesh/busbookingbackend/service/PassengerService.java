package com.mahesh.busbookingbackend.service;

import com.mahesh.busbookingbackend.dtos.PassengerCreateDTO;
import com.mahesh.busbookingbackend.dtos.PassengerResponseDTO;

import java.util.List;

public interface PassengerService {
    PassengerResponseDTO createPassenger(PassengerCreateDTO passengerCreateDTO);
    PassengerResponseDTO updatePassenger(Long id,PassengerCreateDTO passengerCreateDTO);
    List<PassengerResponseDTO> getAllPassengers();
    PassengerResponseDTO getPassenger(Long id);
    void deletePassenger(Long id);
}
