package com.mahesh.busbookingbackend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mahesh.busbookingbackend.audit.BaseEntity;
import com.mahesh.busbookingbackend.enums.BusAmenities;
import com.mahesh.busbookingbackend.enums.BusType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bus")
public class BusEntity extends BaseEntity<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String busName;
    private String busNumber;
    @Enumerated(EnumType.STRING)
    private BusType busType;
    @ElementCollection(targetClass = BusAmenities.class)
    @CollectionTable(name = "bus_amenities", joinColumns = @JoinColumn(name = "bus_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "amenity")
    private List<BusAmenities> busAmenities = new ArrayList<>();
    private String operatorName;
    private String operatorNumber;
    private int totalSeats;

    @OneToMany(mappedBy = "busEntity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<BusScheduleEntity> busSchedules = new ArrayList<>();
}
