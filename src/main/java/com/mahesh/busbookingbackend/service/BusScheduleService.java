package com.mahesh.busbookingbackend.service;

import com.mahesh.busbookingbackend.dtos.BusScheduleCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusScheduleResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface BusScheduleService {
    BusScheduleResponseDTO createSchedule(BusScheduleCreateDTO scheduleCreateDTO);
    BusScheduleResponseDTO updateSchedule(Long id,BusScheduleCreateDTO scheduleCreateDTO);
    BusScheduleResponseDTO getSchedule(Long id);
    List<BusScheduleResponseDTO> getSchedules();
    List<BusScheduleResponseDTO> getSchedules(String source, String destination, LocalDate date);
    void deleteSchedule(Long id);
}
