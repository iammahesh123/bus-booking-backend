package com.mahesh.busbookingbackend.repository;

import com.mahesh.busbookingbackend.entity.BusBookingEntity;
import com.mahesh.busbookingbackend.enums.BookingStatus;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BusBookingRepository extends JpaRepository<BusBookingEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BusBookingEntity b WHERE b.id = :bookingId")
    Optional<BusBookingEntity> findByIdWithLock(@Param("bookingId") Long bookingId);

    List<BusBookingEntity> findByBookingStatusAndExpiryTimeBefore(BookingStatus status, LocalDateTime expiryTime);
}
