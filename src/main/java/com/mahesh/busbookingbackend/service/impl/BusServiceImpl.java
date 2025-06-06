package com.mahesh.busbookingbackend.service.impl;

import com.mahesh.busbookingbackend.dtos.BusCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusResponseDTO;
import com.mahesh.busbookingbackend.entity.BusEntity;
import com.mahesh.busbookingbackend.mapper.BusMapper;
import com.mahesh.busbookingbackend.repository.BusRepository;
import com.mahesh.busbookingbackend.service.BusService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusServiceImpl implements BusService {

    private final BusRepository busRepository;
    private final BusMapper busMapper;
    private final ModelMapper modelMapper;

    @Override
    public BusResponseDTO createBus(BusCreateDTO busCreateDTO) {
        BusEntity busEntity = new BusEntity();
        BeanUtils.copyProperties(busCreateDTO, busEntity);
        BusEntity savedBus = busRepository.save(busEntity);
        return busMapper.toDTO(savedBus,modelMapper);
    }

    @Override
    public BusResponseDTO updateBus(Long id, BusCreateDTO busCreateDTO) {
        BusEntity existingBus = busRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("No such bus")
        );
        BeanUtils.copyProperties(busCreateDTO, existingBus);
        BusEntity updatedBus = busRepository.save(existingBus);
        return busMapper.toDTO(updatedBus,modelMapper);
    }

    @Override
    public BusResponseDTO getBus(Long id) {
        BusEntity busEntity = busRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("No such bus")
        );
        return busMapper.toDTO(busEntity,modelMapper);
    }

    @Override
    public List<BusResponseDTO> getBuss() {
        List<BusEntity> busEntities = busRepository.findAll();
        return busEntities.stream().map(busEntity -> busMapper.toDTO(busEntity,modelMapper)).collect(Collectors.toList());
    }

    @Override
    public List<BusResponseDTO> getBussByStatus(String status) {
        return List.of();
    }

    @Override
    public List<BusResponseDTO> searchBuses() {
        return List.of();
    }

    @Override
    public void deleteBus(Long id) {
        BusEntity busEntity = busRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("No such bus"));
        busRepository.delete(busEntity);
    }
}
