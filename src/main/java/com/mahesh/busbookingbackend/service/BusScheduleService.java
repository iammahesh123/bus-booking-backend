package com.mahesh.busbookingbackend.service;

import com.mahesh.busbookingbackend.dtos.BusScheduleCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusScheduleResponseDTO;
import com.mahesh.busbookingbackend.dtos.PageModel;
import com.mahesh.busbookingbackend.dtos.PaginationResponseModel;

import java.time.LocalDate;
import java.util.List;

public interface BusScheduleService {
    BusScheduleResponseDTO createSchedule(BusScheduleCreateDTO scheduleCreateDTO);
    BusScheduleResponseDTO updateSchedule(Long id,BusScheduleCreateDTO scheduleCreateDTO);
    BusScheduleResponseDTO getSchedule(Long id);
    PaginationResponseModel<BusScheduleResponseDTO> getSchedules(PageModel pageModel);
    List<BusScheduleResponseDTO> getSchedules(String sourceCity, String destinationCity, LocalDate scheduleDate);
    void deleteSchedule(Long id);
}
