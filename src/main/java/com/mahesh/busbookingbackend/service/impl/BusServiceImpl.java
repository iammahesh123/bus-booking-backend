package com.mahesh.busbookingbackend.service.impl;

import com.mahesh.busbookingbackend.dtos.*;
import com.mahesh.busbookingbackend.entity.BusEntity;
import com.mahesh.busbookingbackend.exception.ResourceNotFoundException;
import com.mahesh.busbookingbackend.mapper.BusMapper;
import com.mahesh.busbookingbackend.repository.BusRepository;
import com.mahesh.busbookingbackend.service.BusService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.mahesh.busbookingbackend.utility.PaginationUtility.applyPagination;

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
                () -> new ResourceNotFoundException("No such bus")
        );
        BeanUtils.copyProperties(busCreateDTO, existingBus);
        BusEntity updatedBus = busRepository.save(existingBus);
        return busMapper.toDTO(updatedBus,modelMapper);
    }

    @Override
    public BusResponseDTO getBus(Long id) {
        BusEntity busEntity = busRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No such bus")
        );
        return busMapper.toDTO(busEntity,modelMapper);
    }

    @Override
    public PaginationResponseModel<BusResponseDTO> getBuss(PageModel pageModel) {
        Pageable pageable = applyPagination(pageModel);
        Page<BusEntity> busEntities = busRepository.findAll(pageable);
        List<BusResponseDTO> busResponseDTOS = busEntities.stream().map(busEntity -> busMapper.toDTO(busEntity,modelMapper)).collect(Collectors.toList());
        PaginationResponseModel<BusResponseDTO> paginationResponseModel = new PaginationResponseModel<>();
        paginationResponseModel.setData(busResponseDTOS);
        paginationResponseModel.setTotalRecords(busEntities.getTotalElements());
        paginationResponseModel.setTotalPages(busEntities.getTotalPages());
        return paginationResponseModel;
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
                () -> new ResourceNotFoundException("No such bus"));
        busRepository.delete(busEntity);
    }
}
