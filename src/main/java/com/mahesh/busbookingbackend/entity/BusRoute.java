package com.mahesh.busbookingbackend.entity;

import com.mahesh.busbookingbackend.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bus_route")
@ToString(exclude = "busStops")
public class BusRoute extends BaseEntity<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sourceCity;
    private String destinationCity;
    private int totalDistance;
    private String totalDuration;

    @OneToMany(mappedBy = "busRoute", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BusStops> busStops = new ArrayList<>();

    @OneToMany(mappedBy = "busRoute", cascade = CascadeType.ALL)
    private List<BusScheduleEntity> busSchedules = new ArrayList<>();
}
