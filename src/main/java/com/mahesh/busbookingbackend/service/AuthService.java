package com.mahesh.busbookingbackend.service;

import com.mahesh.busbookingbackend.dtos.AuthResponseDTO;
import com.mahesh.busbookingbackend.dtos.UserRegisterDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    AuthResponseDTO login(String username, String password);
    AuthResponseDTO register(UserRegisterDTO user);
    AuthResponseDTO refreshToken(String refreshToken);
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
    void setRefreshTokenCookie(HttpServletResponse response, String refreshToken);
    void expireRefreshTokenCookie(HttpServletResponse response);
}
