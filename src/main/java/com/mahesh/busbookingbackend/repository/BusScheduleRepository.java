package com.mahesh.busbookingbackend.repository;

import com.mahesh.busbookingbackend.entity.BusScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BusScheduleRepository extends JpaRepository<BusScheduleEntity, Long> {
    @Query("SELECT s FROM BusScheduleEntity s " +
            "JOIN s.busRoute r " +
            "WHERE LOWER(r.sourceCity) = LOWER(:source) " +
            "AND LOWER(r.destinationCity) = LOWER(:destination) " +
            "AND s.scheduleDate = :date")
    List<BusScheduleEntity> findBySourceAndDestinationAndDate(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("date") LocalDate date
    );
}
