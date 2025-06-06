package com.mahesh.busbookingbackend.service;

import com.mahesh.busbookingbackend.dtos.AuthResponseDTO;
import com.mahesh.busbookingbackend.dtos.UserRegisterDTO;

public interface AuthService {
    AuthResponseDTO login(String username, String password);
    AuthResponseDTO register(UserRegisterDTO user);
    AuthResponseDTO refreshToken(String refreshToken);
}
