package com.mahesh.busbookingbackend.service.impl;

import com.mahesh.busbookingbackend.dtos.BusStopCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusStopResponseDTO;
import com.mahesh.busbookingbackend.entity.BusStops;
import com.mahesh.busbookingbackend.exception.ResourceNotFoundException;
import com.mahesh.busbookingbackend.mapper.BusStopMapper;
import com.mahesh.busbookingbackend.repository.BusStopRepository;
import com.mahesh.busbookingbackend.service.BusStopService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusStopServiceImpl implements BusStopService {

    private final BusStopRepository busStopRepository;
    private final BusStopMapper busStopMapper;
    private final ModelMapper modelMapper;

    public BusStopServiceImpl(BusStopRepository busStopRepository, BusStopMapper busStopMapper, ModelMapper modelMapper) {
        this.busStopRepository = busStopRepository;
        this.busStopMapper = busStopMapper;
        this.modelMapper = modelMapper;
    }

    @Override
    public BusStopResponseDTO createBusStop(BusStopCreateDTO busStopCreateDTO) {
        BusStops busStops = new BusStops();
        BeanUtils.copyProperties(busStopCreateDTO, busStops);
        BusStops savedStops = busStopRepository.save(busStops);
        return busStopMapper.toDTO(savedStops,modelMapper);
    }

    @Override
    public BusStopResponseDTO updateBusStop(Long id, BusStopCreateDTO busStopCreateDTO) {
        BusStops existingStop = busStopRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No such stop exists: " + id));
        BeanUtils.copyProperties(busStopCreateDTO, existingStop);
        BusStops updatedStop = busStopRepository.save(existingStop);
        return busStopMapper.toDTO(updatedStop,modelMapper);
    }

    @Override
    public BusStopResponseDTO getBusStop(Long id) {
        BusStops existingStop = busStopRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No such stop exists: " + id));
        return busStopMapper.toDTO(existingStop,modelMapper);
    }

    @Override
    public List<BusStopResponseDTO> getBusStops() {
        List<BusStops> allStops = busStopRepository.findAll();
        return allStops.stream().map(stop -> busStopMapper.toDTO(stop,modelMapper)).collect(Collectors.toList());
    }

    @Override
    public void deleteBusStop(Long id) {
        BusStops existingStop = busStopRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No such stop exists: " + id));
        busStopRepository.delete(existingStop);
    }
}
