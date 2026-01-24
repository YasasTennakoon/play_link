package com.example.play_link.application.auth;

import com.example.play_link.api.auth.dto.AuthResponse;
import com.example.play_link.api.auth.dto.LoginRequest;
import com.example.play_link.api.auth.dto.RegisterRequest;
import com.example.play_link.domain.auth.RefreshToken;
import com.example.play_link.domain.user.User;
import com.example.play_link.infrastructure.security.JwtUtil;
import com.example.play_link.infrastructure.user.UserRepository;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.authentication.BadCredentialsException;

import com.example.play_link.application.common.exceptions.InvalidCredentialsException;
import com.example.play_link.application.common.exceptions.TokenRefreshException;
import com.example.play_link.application.common.exceptions.UserAlreadyExistsException;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, @Lazy AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        return new org.springframework.security.core.userdetails.User(
            user.getUserName(),
            user.getPassword(),
            getAuthorities(user)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return user.getUserRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    /*
     User registration method.
     Request: RegisterRequest containing user details.
     Note: Refresh token is created but not returned in response - it's set as httpOnly cookie
    */
    public AuthResponse register(RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Registration request cannot be null");
        }
        
        //Checking weather the user is already registered
        if (userRepository.findByUserName(request.getUserName()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUserName());
        }

        // Checking whether the user email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already registered: " + request.getEmail());
        }

        // After the validation checks proceed with the user registration.
        User user = User.builder()
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userRoles(request.getUserRoles())
                .enabled(false)
                .build();

        User savedUser = userRepository.save(user);

        // Generate JWT access token
        UserDetails userDetails = loadUserByUsername(savedUser.getUserName());
        String accessToken = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .user(savedUser)
                .build();
    }

    /*
     User login method.
     Request: LoginRequest containing username and password.
     Note: Refresh token is created but not returned in response - it's set as httpOnly cookie
    */
    public AuthResponse login(LoginRequest request) {
        // Validate request is not null
        if (request == null) {
            throw new IllegalArgumentException("Login request cannot be null");
        }
        
        // Check if user exists and is enabled before authentication
        User user = userRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));
        
        try {
                 authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName(),
                        request.getPassword()
                )
        );   
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        // Generate JWT access token
        UserDetails userDetails = loadUserByUsername(user.getUserName());
        String accessToken = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .user(user)
                .build();
    }

    /*
     Refresh access token using refresh token from cookie.
     Request: Refresh token string from httpOnly cookie.
     Response: AuthResponse containing new JWT access token.
    */
    public AuthResponse refreshAccessToken(String refreshTokenStr) {
        return refreshTokenService.findByToken(refreshTokenStr)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    UserDetails userDetails = loadUserByUsername(user.getUserName());
                    String newAccessToken = jwtUtil.generateToken(userDetails);
                    return AuthResponse.builder()
                            .accessToken(newAccessToken)
                            .user(user)
                            .build();
                })
                .orElseThrow(() -> new TokenRefreshException("Invalid refresh token"));
    }

    /*
     Get refresh token for a user.
     Returns the refresh token string to be set in httpOnly cookie.
    */
    public String getRefreshToken(String username) {
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(username);
        return refreshToken.getToken();
    }

    /*
     Logout user by deleting their refresh token.
    */
    public void logout(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        refreshTokenService.deleteByUser(user);
    }
}
