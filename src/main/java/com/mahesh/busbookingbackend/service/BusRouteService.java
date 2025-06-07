package com.mahesh.busbookingbackend.service;

import com.mahesh.busbookingbackend.dtos.BusRouteCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusRouteResponseDTO;
import com.mahesh.busbookingbackend.dtos.PageModel;
import com.mahesh.busbookingbackend.dtos.PaginationResponseModel;

import java.util.List;

public interface BusRouteService {
    BusRouteResponseDTO createRoute(BusRouteCreateDTO busRouteCreateDTO);
    BusRouteResponseDTO updateRoute(Long id,BusRouteCreateDTO busRouteCreateDTO);
    BusRouteResponseDTO getRoute(Long id);
    PaginationResponseModel<BusRouteResponseDTO> getRoutes(PageModel pageModel);
    void deleteRoute(Long id);
    List<String> getAllUniqueCities();
}
