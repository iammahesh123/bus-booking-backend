package com.mahesh.busbookingbackend.repository;

import com.fasterxml.jackson.databind.introspect.AnnotationCollector;
import com.mahesh.busbookingbackend.entity.UserEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email);
    UserEntity findByPasswordResetToken(String token);
}
