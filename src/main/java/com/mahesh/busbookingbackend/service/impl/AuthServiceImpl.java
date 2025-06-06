package com.mahesh.busbookingbackend.service.impl;

import com.mahesh.busbookingbackend.dtos.AuthResponseDTO;
import com.mahesh.busbookingbackend.dtos.UserRegisterDTO;
import com.mahesh.busbookingbackend.entity.UserEntity;
import com.mahesh.busbookingbackend.repository.UserRepository;
import com.mahesh.busbookingbackend.security.JwtUtil;
import com.mahesh.busbookingbackend.service.AuthService;
import com.mahesh.busbookingbackend.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
        // Check if email already exists
        if (userData != null) {
            throw new RuntimeException("Email is already registered");
        }

        UserEntity user = new UserEntity();
        user.setFullName(userRegisterDTO.getFullName());
        user.setRole(userRegisterDTO.getRole());
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setEmail(userRegisterDTO.getEmail());
        user.setPhoneNumber(userRegisterDTO.getPhoneNumber());
        user.setFullName(userRegisterDTO.getFullName());
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), String.valueOf(user.getRole()));
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        // Send welcome email
        String subject = "Welcome to Blue Bus!";
        String message = String.format(
                "Hello %s,\n\nThank you for choosing <strong>Blue Bus</strong> for your travel needs\nEnjoy seamless bus bookings with us!\nBest Regards,\nBlueBus Team",
                user.getFullName()
        );
        emailService.sendEmail(user.getEmail(), subject, message);

        return new AuthResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getPhoneNumber(),
                accessToken,
                refreshToken
        );
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

        return new AuthResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getPhoneNumber(),
                accessToken,
                refreshToken
        );
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

        return new AuthResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getPhoneNumber(),
                newAccessToken,
                newRefreshToken
        );
    }
}