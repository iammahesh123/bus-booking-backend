package com.mahesh.busbookingbackend.service;

import com.mahesh.busbookingbackend.dtos.SeatCreateDTO;
import com.mahesh.busbookingbackend.dtos.SeatDTO;

import java.util.List;

public interface SeatService {
    List<SeatDTO> generateSeats(long scheduleId);
    List<SeatDTO> viewSeats(long scheduleId);
    SeatDTO createSeat(SeatCreateDTO seatCreateDTO);
    SeatDTO updateSeat(Long id, SeatCreateDTO seatCreateDTO);
    SeatDTO getSeat(Long id);
    List<SeatDTO> getAllSeats();
    void deleteSeat(Long id);
    SeatDTO lockSeat(Long scheduleId, Long seatNumber);

}
