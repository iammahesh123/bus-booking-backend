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
public class BusBookingCreateDTO {
    private String userId;
    private LocalDate bookingDate;
    private double totalPrice;
    private BookingStatus bookingStatus;
    private PaymentStatus paymentStatus;
    private Set<Long> seatIds;
    private List<Long> passengerIds;
}
