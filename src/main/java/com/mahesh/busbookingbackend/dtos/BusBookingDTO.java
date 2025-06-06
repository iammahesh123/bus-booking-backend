package com.mahesh.busbookingbackend.dtos;

import com.mahesh.busbookingbackend.enums.BookingStatus;
import com.mahesh.busbookingbackend.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusBookingDTO {
    private Long id;
    private String userId;
    private LocalDate bookingDate;
    private double totalPrice;
    private BookingStatus bookingStatus;
    private PaymentStatus paymentStatus;
    private Set<SeatDTO> seatDTOS;
    private List<PassengerDTO> passengerDTOS;
    private String createdAt;
    private String updatedAt;
    private String updatedBy;
    private String createdBy;
}
