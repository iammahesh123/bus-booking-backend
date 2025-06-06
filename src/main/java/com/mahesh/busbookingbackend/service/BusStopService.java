package com.mahesh.busbookingbackend.service;

import com.mahesh.busbookingbackend.dtos.BusStopCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusStopResponseDTO;

import java.util.List;

public interface BusStopService {
    BusStopResponseDTO createBusStop(BusStopCreateDTO busStopCreateDTO);
    BusStopResponseDTO updateBusStop(Long id,BusStopCreateDTO busStopCreateDTO);
    BusStopResponseDTO getBusStop(Long id);
    List<BusStopResponseDTO> getBusStops();
    void deleteBusStop(Long id);
}
