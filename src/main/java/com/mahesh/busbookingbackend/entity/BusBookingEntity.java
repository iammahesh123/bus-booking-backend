package com.mahesh.busbookingbackend.entity;

import com.mahesh.busbookingbackend.audit.BaseEntity;
import com.mahesh.busbookingbackend.enums.BookingStatus;
import com.mahesh.busbookingbackend.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class BusBookingEntity extends BaseEntity<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bookingNumber;
    private String userId;
    private LocalDate bookingDate;
    private double totalPrice;
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @OneToMany(mappedBy = "",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Set<SeatEntity> seats = new HashSet<>();

    @OneToMany(mappedBy = "busBooking",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private List<PassengerEntity> passengers = new ArrayList<>();

}
