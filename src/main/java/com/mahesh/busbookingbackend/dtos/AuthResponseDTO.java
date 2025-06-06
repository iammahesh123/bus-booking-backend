package com.mahesh.busbookingbackend.dtos;

import com.mahesh.busbookingbackend.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private String phoneNumber;
    private String accessToken;
    private String refreshToken;
}