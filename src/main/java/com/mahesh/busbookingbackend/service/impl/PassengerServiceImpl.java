package com.mahesh.busbookingbackend.service.impl;

import com.mahesh.busbookingbackend.dtos.PassengerCreateDTO;
import com.mahesh.busbookingbackend.dtos.PassengerResponseDTO;
import com.mahesh.busbookingbackend.entity.PassengerEntity;
import com.mahesh.busbookingbackend.exception.ResourceNotFoundException;
import com.mahesh.busbookingbackend.mapper.PassengerMapper;
import com.mahesh.busbookingbackend.repository.PassengerRepository;
import com.mahesh.busbookingbackend.service.PassengerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;
    private final ModelMapper modelMapper;

    public PassengerServiceImpl(PassengerRepository passengerRepository, PassengerMapper passengerMapper, ModelMapper modelMapper) {
        this.passengerRepository = passengerRepository;
        this.passengerMapper = passengerMapper;
        this.modelMapper = modelMapper;
    }

    @Override
    public PassengerResponseDTO createPassenger(PassengerCreateDTO passengerCreateDTO) {
        PassengerEntity passengerEntity = new PassengerEntity();
        BeanUtils.copyProperties(passengerCreateDTO, passengerEntity);
        PassengerEntity savedPassenger = passengerRepository.save(passengerEntity);
        return passengerMapper.toDTO(savedPassenger,modelMapper);
    }

    @Override
    public PassengerResponseDTO updatePassenger(Long id, PassengerCreateDTO passengerCreateDTO) {
        PassengerEntity existingPassenger = passengerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Passenger not found with id: " + id));
        BeanUtils.copyProperties(passengerCreateDTO, existingPassenger);
        PassengerEntity savedPassenger = passengerRepository.save(existingPassenger);
        return passengerMapper.toDTO(savedPassenger,modelMapper);
    }

    @Override
    public List<PassengerResponseDTO> getAllPassengers() {
        List<PassengerEntity> passengers = passengerRepository.findAll();
        return passengers.stream().map(passengerEntity -> passengerMapper.toDTO(passengerEntity,modelMapper)).collect(Collectors.toList());
    }

    @Override
    public PassengerResponseDTO getPassenger(Long id) {
        PassengerEntity passenger = passengerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Passenger not found with id: " + id));
        return passengerMapper.toDTO(passenger,modelMapper);
    }

    @Override
    public void deletePassenger(Long id) {
        PassengerEntity passenger = passengerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Passenger not found with id: " + id));
        passengerRepository.delete(passenger);
    }
}
