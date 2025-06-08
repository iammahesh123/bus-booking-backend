package com.mahesh.busbookingbackend.repository;

import com.mahesh.busbookingbackend.entity.SeatEntity;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
    List<SeatEntity> findByBusScheduleId(Long busScheduleId);
    // Pessimistic lock for seat
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SeatEntity s WHERE s.busSchedule.id = :scheduleId AND s.id = :seatNumber")
    Optional<SeatEntity> findSeatForBookingWithLock(@Param("scheduleId") Long scheduleId, @Param("seatNumber") Long seatNumber);

    // Check seat availability
    @Query("SELECT s FROM SeatEntity s WHERE s.busSchedule.id = :scheduleId AND s.seatNumber = :seatNumber AND s.seatStatus = 'AVAILABLE'")
    Optional<SeatEntity> findAvailableSeat(@Param("scheduleId") Long scheduleId, @Param("seatNumber") String seatNumber);
}
