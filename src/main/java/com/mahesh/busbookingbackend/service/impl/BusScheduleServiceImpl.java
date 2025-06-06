package com.mahesh.busbookingbackend.service.impl;

import com.mahesh.busbookingbackend.dtos.BusScheduleCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusScheduleResponseDTO;
import com.mahesh.busbookingbackend.entity.BusEntity;
import com.mahesh.busbookingbackend.entity.BusRoute;
import com.mahesh.busbookingbackend.entity.BusScheduleEntity;
import com.mahesh.busbookingbackend.mapper.BusScheduleMapper;
import com.mahesh.busbookingbackend.repository.BusRepository;
import com.mahesh.busbookingbackend.repository.BusRouteRepository;
import com.mahesh.busbookingbackend.repository.BusScheduleRepository;
import com.mahesh.busbookingbackend.service.BusScheduleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusScheduleServiceImpl implements BusScheduleService {

    private final BusScheduleRepository busScheduleRepository;
    private final BusScheduleMapper busScheduleMapper;
    private final ModelMapper modelMapper;
    private final BusRepository busRepository;
    private final BusRouteRepository busRouteRepository;

    public BusScheduleServiceImpl(BusScheduleRepository busScheduleRepository, BusScheduleMapper busScheduleMapper, ModelMapper modelMapper, BusRepository busRepository, BusRouteRepository busRouteRepository) {
        this.busScheduleRepository = busScheduleRepository;
        this.busScheduleMapper = busScheduleMapper;
        this.modelMapper = modelMapper;
        this.busRepository = busRepository;
        this.busRouteRepository = busRouteRepository;
    }

    @Override
    public BusScheduleResponseDTO createSchedule(BusScheduleCreateDTO scheduleCreateDTO) {
        BusScheduleEntity scheduleEntity = new BusScheduleEntity();
        BusEntity bus = busRepository.findById(scheduleCreateDTO.getBusId()).orElse(null);
        BusRoute route = busRouteRepository.findById(scheduleCreateDTO.getRouteId()).orElse(null);
        BeanUtils.copyProperties(scheduleCreateDTO, scheduleEntity);
        BusScheduleEntity savedSchedule = busScheduleRepository.save(scheduleEntity);
        savedSchedule.setBusEntity(bus);
        savedSchedule.setBusRoute(route);
        busScheduleRepository.save(savedSchedule);
        return busScheduleMapper.toDTO(savedSchedule,modelMapper);
    }

    @Override
    public BusScheduleResponseDTO updateSchedule(Long id, BusScheduleCreateDTO scheduleCreateDTO) {
        BusScheduleEntity existingSchedule = busScheduleRepository.findById(id).orElse(null);
        BusEntity bus = busRepository.findById(scheduleCreateDTO.getBusId()).orElse(null);
        BusRoute route = busRouteRepository.findById(scheduleCreateDTO.getRouteId()).orElse(null);
        BeanUtils.copyProperties(scheduleCreateDTO, existingSchedule);
        BusScheduleEntity updatedSchedule = busScheduleRepository.save(existingSchedule);
        updatedSchedule.setBusEntity(bus);
        updatedSchedule.setBusRoute(route);
        busScheduleRepository.save(updatedSchedule);
        return busScheduleMapper.toDTO(updatedSchedule,modelMapper);
    }

    @Override
    public BusScheduleResponseDTO getSchedule(Long id) {
        BusScheduleEntity scheduleEntity = busScheduleRepository.findById(id).orElse(null);
        return busScheduleMapper.toDTO(scheduleEntity,modelMapper);
    }

    @Override
    public List<BusScheduleResponseDTO> getSchedules() {
        List<BusScheduleEntity> list = busScheduleRepository.findAll();
        return list.stream().map(scheduleEntity -> busScheduleMapper.toDTO(scheduleEntity,modelMapper)).collect(Collectors.toList());
    }

    @Override
    public List<BusScheduleResponseDTO> getSchedules(String source, String destination, LocalDate date) {
        List<BusScheduleEntity> schedule =  busScheduleRepository.findBySourceAndDestinationAndDate(source, destination, date);
       return schedule.stream().map(scheduleEntity -> busScheduleMapper.toDTO(scheduleEntity,modelMapper)).collect(Collectors.toList());
    }

    @Override
    public void deleteSchedule(Long id) {
        BusScheduleEntity scheduleEntity = busScheduleRepository.findById(id).orElse(null);
        busScheduleRepository.delete(scheduleEntity);
    }
}
