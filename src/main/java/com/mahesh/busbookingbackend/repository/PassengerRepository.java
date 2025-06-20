package com.mahesh.busbookingbackend.repository;

import com.mahesh.busbookingbackend.entity.PassengerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends JpaRepository<PassengerEntity, Long> {
}
