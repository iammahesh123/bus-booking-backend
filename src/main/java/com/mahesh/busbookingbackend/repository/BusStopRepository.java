package com.mahesh.busbookingbackend.repository;

import com.mahesh.busbookingbackend.entity.BusStops;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusStopRepository extends JpaRepository<BusStops, Long> {
}
