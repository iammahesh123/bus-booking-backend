package com.mahesh.busbookingbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mahesh.busbookingbackend.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bus_schedule")
public class BusScheduleEntity extends BaseEntity<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bus_id")
    @ToString.Exclude
    private BusEntity busEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id")
    @ToString.Exclude
    private BusRoute busRoute;

    private LocalDate scheduleDate;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private int totalSeats;
    private int farePrice;

    @OneToMany(mappedBy = "busSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<SeatEntity> seats = new ArrayList<>();


}
