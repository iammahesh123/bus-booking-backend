package com.mahesh.busbookingbackend.service.impl;

import com.mahesh.busbookingbackend.dtos.BusScheduleCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusScheduleResponseDTO;
import com.mahesh.busbookingbackend.dtos.PageModel;
import com.mahesh.busbookingbackend.dtos.PaginationResponseModel;
import com.mahesh.busbookingbackend.entity.BusEntity;
import com.mahesh.busbookingbackend.entity.BusRoute;
import com.mahesh.busbookingbackend.entity.BusScheduleEntity;
import com.mahesh.busbookingbackend.exception.ResourceNotFoundException;
import com.mahesh.busbookingbackend.mapper.BusScheduleMapper;
import com.mahesh.busbookingbackend.repository.BusRepository;
import com.mahesh.busbookingbackend.repository.BusRouteRepository;
import com.mahesh.busbookingbackend.repository.BusScheduleRepository;
import com.mahesh.busbookingbackend.service.BusScheduleService;
import com.mahesh.busbookingbackend.service.ScheduleAutomationService;
import com.mahesh.busbookingbackend.service.SeatService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.mahesh.busbookingbackend.utility.PaginationUtility.applyPagination;

@Slf4j
@Service
public class BusScheduleServiceImpl implements BusScheduleService {

    private final BusScheduleRepository busScheduleRepository;
    private final BusScheduleMapper busScheduleMapper;
    private final ModelMapper modelMapper;
    private final BusRepository busRepository;
    private final BusRouteRepository busRouteRepository;
    private final ScheduleAutomationService scheduleAutomationService;
    private final SeatService seatService;

    public BusScheduleServiceImpl(BusScheduleRepository busScheduleRepository, BusScheduleMapper busScheduleMapper, ModelMapper modelMapper, BusRepository busRepository, BusRouteRepository busRouteRepository, ScheduleAutomationService scheduleAutomationService, SeatService seatService) {
        this.busScheduleRepository = busScheduleRepository;
        this.busScheduleMapper = busScheduleMapper;
        this.modelMapper = modelMapper;
        this.busRepository = busRepository;
        this.busRouteRepository = busRouteRepository;
        this.scheduleAutomationService = scheduleAutomationService;
        this.seatService = seatService;
    }

    @Override
    @Transactional
    public BusScheduleResponseDTO createSchedule(BusScheduleCreateDTO scheduleCreateDTO) {
        log.info("Received request to create schedule. Master flag: {}", scheduleCreateDTO.isMasterRecord());

        BusEntity bus = busRepository.findById(scheduleCreateDTO.getBusId()).orElseThrow(
                () -> new ResourceNotFoundException("Bus not found with id: " + scheduleCreateDTO.getBusId()));
        BusRoute route = busRouteRepository.findById(scheduleCreateDTO.getRouteId()).orElseThrow(
                () -> new ResourceNotFoundException("BusRoute not found with id: " + scheduleCreateDTO.getRouteId()));

        BusScheduleEntity scheduleEntity = new BusScheduleEntity();
        BeanUtils.copyProperties(scheduleCreateDTO, scheduleEntity);
        scheduleEntity.setBusEntity(bus);
        scheduleEntity.setBusRoute(route);
        scheduleEntity.setTotalSeats(bus.getTotalSeats());

        if (scheduleCreateDTO.isMasterRecord()) {
            if (scheduleCreateDTO.getAutomationDuration() == null) {
                throw new ResourceNotFoundException("AutomationDuration must be provided for a master schedule.");
            }
            if (busScheduleRepository.existsByBusEntityIdAndBusRouteIdAndScheduleDate(bus.getId(), route.getId(), scheduleCreateDTO.getScheduleDate())) {
                throw new ResourceNotFoundException("An automated schedule already exists for this bus and route.");
            }
            scheduleEntity.setMasterRecord(true);
            scheduleEntity.setAutomationDuration(scheduleCreateDTO.getAutomationDuration());
        }

        BusScheduleEntity savedSchedule = busScheduleRepository.save(scheduleEntity);
        log.info("Schedule created with ID: {}", savedSchedule.getId());
        seatService.generateSeats(savedSchedule.getId());
        log.info("Generating the seats for the schedule with ID: {}", savedSchedule.getId());

        if (savedSchedule.isMasterRecord()) {
            // This is the critical new step for your requirement
            log.info("Triggering initial large-scale generation for new master record...");
            scheduleAutomationService.performInitialGeneration(savedSchedule);
        }
        return busScheduleMapper.toDTO(savedSchedule, modelMapper);
    }

    @Override
    public BusScheduleResponseDTO updateSchedule(Long id, BusScheduleCreateDTO scheduleCreateDTO) {
        BusScheduleEntity existingSchedule = busScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + id));

        // Prevent updating a generated schedule's core details, or master's automation rules
        if (existingSchedule.isMasterRecord() || !existingSchedule.getSeats().isEmpty()){
            log.warn("Attempted to update a locked schedule (master or with bookings). ID: {}", id);
            // Or throw an exception
            // throw new InvalidRequestException("Master schedules or schedules with bookings cannot be updated this way.");
        }

        BusEntity bus = busRepository.findById(scheduleCreateDTO.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + scheduleCreateDTO.getBusId()));
        BusRoute route = busRouteRepository.findById(scheduleCreateDTO.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("BusRoute not found with id: " + scheduleCreateDTO.getRouteId()));

        BeanUtils.copyProperties(scheduleCreateDTO, existingSchedule);
        existingSchedule.setBusEntity(bus);
        existingSchedule.setBusRoute(route);

        BusScheduleEntity updatedSchedule = busScheduleRepository.save(existingSchedule);
        return busScheduleMapper.toDTO(updatedSchedule, modelMapper);
    }


    @Override
    public BusScheduleResponseDTO getSchedule(Long id) {
        BusScheduleEntity scheduleEntity = busScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + id));
        return busScheduleMapper.toDTO(scheduleEntity,modelMapper);
    }

    @Override
    public PaginationResponseModel<BusScheduleResponseDTO> getSchedules(PageModel pageModel) {
        Pageable pageable = applyPagination(pageModel);
        Page<BusScheduleEntity> list = busScheduleRepository.findAll(pageable);
        List<BusScheduleResponseDTO> busScheduleResponseDTOS = list.stream().map(scheduleEntity -> busScheduleMapper.toDTO(scheduleEntity,modelMapper)).toList();
        PaginationResponseModel<BusScheduleResponseDTO> paginationResponseModel = new PaginationResponseModel<>();
        paginationResponseModel.setTotalRecords(list.getTotalElements());
        paginationResponseModel.setTotalPages(list.getTotalPages());
        paginationResponseModel.setData(busScheduleResponseDTOS);
        return paginationResponseModel;
    }

    @Override
    public List<BusScheduleResponseDTO> getSchedules(String source, String destination, LocalDate date) {
        List<BusScheduleEntity> schedule = busScheduleRepository.findBySourceAndDestinationAndDate(source, destination, date);
        log.info(schedule.toString());
        return schedule.stream()
                .map(scheduleEntity -> busScheduleMapper.toDTO(scheduleEntity, modelMapper))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSchedule(Long id) {
        BusScheduleEntity scheduleEntity = busScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + id));

        if (scheduleEntity.isMasterRecord()) {
            throw new ResourceNotFoundException("Master records cannot be deleted. Please use a 'cancel automation' endpoint instead.");
        }
        busScheduleRepository.delete(scheduleEntity);
        log.info("Deleted schedule with id: {}", id);
    }
}
