package com.mahesh.busbookingbackend.service.impl;


import com.mahesh.busbookingbackend.dtos.BusRouteCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusRouteResponseDTO;
import com.mahesh.busbookingbackend.entity.BusRoute;
import com.mahesh.busbookingbackend.entity.BusStops;
import com.mahesh.busbookingbackend.mapper.BusRouteMapper;
import com.mahesh.busbookingbackend.repository.BusRouteRepository;
import com.mahesh.busbookingbackend.repository.BusStopRepository;
import com.mahesh.busbookingbackend.service.BusRouteService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusRouteServiceImpl implements BusRouteService {
    private final BusRouteRepository busRouteRepository;
    private final BusRouteMapper busRouteMapper;
    private final ModelMapper modelMapper;
    private final BusStopRepository busStopRepository;

    @Override
    public BusRouteResponseDTO createRoute(BusRouteCreateDTO busRouteCreateDTO) {
        BusRoute busRoute = new BusRoute();
        BeanUtils.copyProperties(busRouteCreateDTO, busRoute);
        if (busRouteCreateDTO.getStopIds() != null && !busRouteCreateDTO.getStopIds().isEmpty()) {
            List<BusStops> stops = new ArrayList<>();
            for (Long stopId : busRouteCreateDTO.getStopIds()) {
                BusStops stop = busStopRepository.findById(stopId)
                        .orElseThrow(() -> new IllegalArgumentException("Bus stop not found with id: " + stopId));
                stop.setBusRoute(busRoute);
                stops.add(stop);
            }
            busRoute.setBusStops(stops);
        }
        BusRoute savedRoute = busRouteRepository.save(busRoute);
        return busRouteMapper.toDTO(savedRoute, modelMapper);
    }

    @Override
    public BusRouteResponseDTO updateRoute(Long id, BusRouteCreateDTO busRouteCreateDTO) {
        BusRoute existingRoute = busRouteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
        BeanUtils.copyProperties(busRouteCreateDTO, existingRoute);
        if (busRouteCreateDTO.getStopIds() != null) {
            List<BusStops> stops = new ArrayList<>();
            for (Long stopId : busRouteCreateDTO.getStopIds()) {
                BusStops stop = busStopRepository.findById(stopId)
                        .orElseThrow(() -> new IllegalArgumentException("Bus stop not found with id: " + stopId));
                stop.setBusRoute(existingRoute);
                stops.add(stop);
            }
            existingRoute.setBusStops(stops);
        }
        BusRoute updatedRoute = busRouteRepository.save(existingRoute);
        return busRouteMapper.toDTO(updatedRoute, modelMapper);
    }

    @Override
    public BusRouteResponseDTO getRoute(Long id) {
        BusRoute busRoute = busRouteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
        return busRouteMapper.toDTO(busRoute, modelMapper);
    }

    @Override
    public List<BusRouteResponseDTO> getRoutes() {
        List<BusRoute> busRoutes = busRouteRepository.findAll();
        return busRoutes.stream().map(busRoute -> busRouteMapper.toDTO(busRoute, modelMapper)).collect(Collectors.toList());
    }

    @Override
    public void deleteRoute(Long id) {
        BusRoute busRoute = busRouteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
        busRouteRepository.delete(busRoute);
    }
}
