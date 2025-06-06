package com.mahesh.busbookingbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String phoneNumber;
    private String accessToken;
    private String refreshToken;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
