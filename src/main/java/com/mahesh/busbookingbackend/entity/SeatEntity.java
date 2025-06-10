package com.mahesh.busbookingbackend.entity;

import com.mahesh.busbookingbackend.audit.BaseEntity;
import com.mahesh.busbookingbackend.enums.SeatStatus;
import com.mahesh.busbookingbackend.enums.SeatType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@EqualsAndHashCode(exclude = {"busBooking"})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "passenger_seat")
public class SeatEntity extends BaseEntity<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String seatNumber;
    @Enumerated(EnumType.STRING)
    private SeatType seatType;
    @Enumerated(EnumType.STRING)
    @Column(name = "seat_status", length = 20)
    private SeatStatus seatStatus;
    private int seatPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_schedule_id")
    private BusScheduleEntity busSchedule;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id")
    private BusBookingEntity busBooking;
}
