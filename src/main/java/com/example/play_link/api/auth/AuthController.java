package com.example.play_link.api.auth;

import com.example.play_link.api.auth.dto.AuthResponse;
import com.example.play_link.api.auth.dto.LoginRequest;
import com.example.play_link.api.auth.dto.RegisterRequest;
import com.example.play_link.application.auth.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenDuration;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.register(request);
        
        // Create and set httpOnly cookie with refresh token
        String refreshToken = authService.getRefreshToken(request.getUserName());
        setRefreshTokenCookie(response, refreshToken);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        
        // Create and set httpOnly cookie with refresh token
        String refreshToken = authService.getRefreshToken(request.getUserName());
        setRefreshTokenCookie(response, refreshToken);
        
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request) {
        String refreshToken = getRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        AuthResponse authResponse = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            authService.logout(username);
        }
        
        // Clear refresh token cookie
        clearRefreshTokenCookie(response);
        
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is running");
    }

    // Helper method to set httpOnly cookie
    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);  // Not accessible via JavaScript
        cookie.setSecure(false);    // Set to true in production with HTTPS
        cookie.setPath("/auth");    // Only sent to /auth endpoints
        cookie.setMaxAge((int) (refreshTokenDuration / 1000)); // Convert to seconds
        response.addCookie(cookie);
    }

    // Helper method to get refresh token from cookie
    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    // Helper method to clear refresh token cookie
    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);  // Set to true in production with HTTPS
        cookie.setPath("/auth");
        cookie.setMaxAge(0);  // Delete cookie
        response.addCookie(cookie);
    }
}
