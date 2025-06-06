package com.mahesh.busbookingbackend.entity;

import com.mahesh.busbookingbackend.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerEntity extends BaseEntity<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String passengerName;
    private int age;
    private String gender;
    private String seatNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    private BusBookingEntity busBooking;
}
