package com.mahesh.busbookingbackend.service;

import com.mahesh.busbookingbackend.dtos.BusCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusResponseDTO;
import com.mahesh.busbookingbackend.dtos.PageModel;
import com.mahesh.busbookingbackend.dtos.PaginationResponseModel;

import java.util.List;

public interface BusService {
    BusResponseDTO createBus(BusCreateDTO busCreateDTO);
    BusResponseDTO updateBus(Long id, BusCreateDTO busCreateDTO);
    BusResponseDTO getBus(Long id);
    PaginationResponseModel<BusResponseDTO> getBuss(PageModel pageModel);
    List<BusResponseDTO> getBussByStatus(String status);
    List<BusResponseDTO> searchBuses();
    void deleteBus(Long id);
}
