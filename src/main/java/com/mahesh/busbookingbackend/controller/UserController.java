package com.mahesh.busbookingbackend.controller;

import com.mahesh.busbookingbackend.dtos.*;
import com.mahesh.busbookingbackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth Controller", description = "List of Authentication Endpoints")
@RestController
@RequestMapping("/auth-user")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody UserRegisterDTO userRegisterDTO, HttpServletResponse response) {
        AuthResponseDTO authResponse = authService.register(userRegisterDTO);
        authService.setRefreshTokenCookie(response, authResponse.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Login with username and password")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody UserLoginDTO userLoginDTO, HttpServletResponse response) {
        AuthResponseDTO authResponse = authService.login(userLoginDTO.getEmail(), userLoginDTO.getPassword());
        authService.setRefreshTokenCookie(response, authResponse.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Refresh access token")
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDTO> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            throw new RuntimeException("Refresh token is missing");
        }

        AuthResponseDTO authResponse = authService.refreshToken(refreshToken);
        authService.setRefreshTokenCookie(response, authResponse.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Logout user")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.expireRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Forgot password request")
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequestDTO requestDTO) {
        authService.forgotPassword(requestDTO.getEmail());
        return ResponseEntity.ok("If an account with the provided email exists, a password reset link has been sent.");
    }

    @Operation(summary = "Reset password with token")
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestBody ResetPasswordRequestDTO requestDTO) {
        authService.resetPassword(token, requestDTO.getNewPassword());
        return ResponseEntity.ok("Your password has been reset successfully.");
    }
}