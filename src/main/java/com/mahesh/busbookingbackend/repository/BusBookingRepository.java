package com.mahesh.busbookingbackend.repository;

import com.mahesh.busbookingbackend.entity.BusBookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusBookingRepository extends JpaRepository<BusBookingEntity, Long> {
}
