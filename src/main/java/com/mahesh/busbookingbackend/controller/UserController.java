package com.mahesh.busbookingbackend.controller;

import com.mahesh.busbookingbackend.dtos.AuthResponseDTO;
import com.mahesh.busbookingbackend.dtos.UserLoginDTO;
import com.mahesh.busbookingbackend.dtos.UserRegisterDTO;
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
    public ResponseEntity<AuthResponseDTO> register(
            @RequestBody UserRegisterDTO userRegisterDTO,
            HttpServletResponse response
    ) {
        AuthResponseDTO authResponse = authService.register(userRegisterDTO);
        setRefreshTokenCookie(response, authResponse.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Login with username and password")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody UserLoginDTO userLoginDTO,
            HttpServletResponse response
    ) {
        AuthResponseDTO authResponse = authService.login(userLoginDTO.getEmail(), userLoginDTO.getPassword());
        setRefreshTokenCookie(response, authResponse.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Refresh access token")
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDTO> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null) {
            throw new RuntimeException("Refresh token is missing");
        }

        AuthResponseDTO authResponse = authService.refreshToken(refreshToken);
        setRefreshTokenCookie(response, authResponse.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Logout user")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        expireRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Enable in production with HTTPS
        cookie.setPath("/api/auth/refresh-token");
        cookie.setMaxAge((int) (7 * 24 * 60 * 60)); // 7 days
        response.addCookie(cookie);
    }

    private void expireRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth/refresh-token");
        cookie.setMaxAge(0); // Expire immediately
        response.addCookie(cookie);
    }
}