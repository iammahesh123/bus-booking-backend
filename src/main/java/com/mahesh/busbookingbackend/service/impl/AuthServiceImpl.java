package com.mahesh.busbookingbackend.service.impl;

import com.mahesh.busbookingbackend.dtos.AuthResponseDTO;
import com.mahesh.busbookingbackend.dtos.UserRegisterDTO;
import com.mahesh.busbookingbackend.entity.UserEntity;
import com.mahesh.busbookingbackend.exception.ResourceNotFoundException;
import com.mahesh.busbookingbackend.repository.UserRepository;
import com.mahesh.busbookingbackend.security.JwtUtil;
import com.mahesh.busbookingbackend.service.AuthService;
import com.mahesh.busbookingbackend.service.EmailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    @Override
    public AuthResponseDTO register(UserRegisterDTO userRegisterDTO) {

        UserEntity userData = userRepository.findByEmail(userRegisterDTO.getEmail());
        if (userData != null) {
            throw new RuntimeException("Email is already registered");
        }

        UserEntity user = new UserEntity();
        user.setFullName(userRegisterDTO.getFullName());
        user.setRole(userRegisterDTO.getRole());
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setEmail(userRegisterDTO.getEmail());
        user.setPhoneNumber(userRegisterDTO.getPhoneNumber());
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), String.valueOf(user.getRole()));
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        String subject = "Welcome to Blue Bus!";
        String message = String.format(
                "Hello %s,\n\nThank you for choosing <strong>Blue Bus</strong> for your travel needs\nEnjoy seamless bus bookings with us!\nBest Regards,\nBlueBus Team",
                user.getFullName());
        emailService.sendEmail(user.getEmail(), subject, message);
        return new AuthResponseDTO(user.getId(), user.getFullName(), user.getEmail(), user.getRole(), user.getPhoneNumber(), accessToken, refreshToken);
    }

    @Override
    public AuthResponseDTO login(String username, String password) {
        UserEntity user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), String.valueOf(user.getRole()));
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        return new AuthResponseDTO(user.getId(), user.getFullName(), user.getEmail(), user.getRole(), user.getPhoneNumber(), accessToken, refreshToken);
    }

    @Override
    public AuthResponseDTO refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        String username = jwtUtil.extractUsername(refreshToken);
        UserEntity user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), String.valueOf(user.getRole()));
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        return new AuthResponseDTO(user.getId(), user.getFullName(), user.getEmail(), user.getRole(), user.getPhoneNumber(), newAccessToken, newRefreshToken
        );
    }

    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/auth-user/refresh-token");
        cookie.setMaxAge((int) (7 * 24 * 60 * 60));
        response.addCookie(cookie);
    }

    public void expireRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/auth-user/refresh-token");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @Override
    public void forgotPassword(String email) {
        UserEntity user = userRepository.findByEmail(email);
        if (user != null) {
            String token = UUID.randomUUID().toString();
            user.setPasswordResetToken(token);
            user.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(15)); // Token expires in 15 minutes
            userRepository.save(user);
            String resetLink = "http://localhost:5173/reset-password?token=" + token;
            String subject = "Your Password Reset Request for Blue Bus";
            String message = String.format(
                    "Hello %s,\n\nYou requested to reset your password. Please click the link below to set a new password. This link is valid for 15 minutes.\n\n%s\n\nIf you did not request a password reset, please ignore this email.\n\nBest Regards,\nThe Blue Bus Team",
                    user.getFullName(),
                    resetLink
            );
            emailService.sendEmail(user.getEmail(), subject, message);
        }
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        UserEntity user = userRepository.findByPasswordResetToken(token);
        if (user == null) {
            throw new ResourceNotFoundException("Invalid password reset token.");
        }
        if (user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException("Password reset token has expired.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);

        userRepository.save(user);
    }
}