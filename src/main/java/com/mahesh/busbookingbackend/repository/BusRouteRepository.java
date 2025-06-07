package com.mahesh.busbookingbackend.repository;

import com.mahesh.busbookingbackend.entity.BusRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRouteRepository extends JpaRepository<BusRoute, Long> {
    @Query("SELECT DISTINCT br.sourceCity FROM BusRoute br")
    List<String> findAllDistinctSourceCities();

    @Query("SELECT DISTINCT br.destinationCity FROM BusRoute br")
    List<String> findAllDistinctDestinationCities();
}
