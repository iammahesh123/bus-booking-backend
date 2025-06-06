package com.mahesh.busbookingbackend.entity;

import com.mahesh.busbookingbackend.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bus_schedule")
public class BusScheduleEntity extends BaseEntity<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "bus_id")
    private BusEntity busEntity;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "route_id")
    private BusRoute busRoute;

    private LocalDate scheduleDate;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private int totalSeats;
    private int farePrice;

    @OneToMany(mappedBy = "busSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatEntity> seats = new ArrayList<>();
}
