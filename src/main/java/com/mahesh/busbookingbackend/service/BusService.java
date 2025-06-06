package com.mahesh.busbookingbackend.service;

import com.mahesh.busbookingbackend.dtos.BusCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusResponseDTO;

import java.util.List;

public interface BusService {
    BusResponseDTO createBus(BusCreateDTO busCreateDTO);
    BusResponseDTO updateBus(Long id, BusCreateDTO busCreateDTO);
    BusResponseDTO getBus(Long id);
    List<BusResponseDTO> getBuss();
    List<BusResponseDTO> getBussByStatus(String status);
    List<BusResponseDTO> searchBuses();
    void deleteBus(Long id);
}
