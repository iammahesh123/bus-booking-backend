package com.mahesh.busbookingbackend.repository;

import com.mahesh.busbookingbackend.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
    List<SeatEntity> findByBusScheduleId(Long busScheduleId);
}
