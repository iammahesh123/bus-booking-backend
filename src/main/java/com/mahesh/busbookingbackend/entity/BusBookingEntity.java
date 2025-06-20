package com.mahesh.busbookingbackend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mahesh.busbookingbackend.audit.BaseEntity;
import com.mahesh.busbookingbackend.enums.BookingStatus;
import com.mahesh.busbookingbackend.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(exclude = {"seats"}, callSuper = false)
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
    @Column(nullable = false)
    private LocalDateTime expiryTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus bookingStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @OneToMany(mappedBy = "busBooking",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonManagedReference("booking-seats")
    private Set<SeatEntity> seats = new HashSet<>();

    @OneToMany(mappedBy = "busBooking",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonManagedReference("booking-passengers")
    private List<PassengerEntity> passengers = new ArrayList<>();

    @OneToOne
    @ToString.Exclude
    private BusScheduleEntity busSchedule;

    @OneToOne(mappedBy = "busBooking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private PaymentEntity payment;

}
