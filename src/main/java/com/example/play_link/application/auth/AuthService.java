package com.example.play_link.application.auth;

import com.example.play_link.api.auth.dto.AuthResponse;
import com.example.play_link.api.auth.dto.LoginRequest;
import com.example.play_link.api.auth.dto.RegisterRequest;
import com.example.play_link.domain.user.User;
import com.example.play_link.infrastructure.security.JwtUtil;
import com.example.play_link.infrastructure.user.UserRepository;
import lombok.RequiredArgsConstructor;
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

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      JwtUtil jwtUtil,
                      @Lazy AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        return new org.springframework.security.core.userdetails.User(
            user.getUserName(),
            user.getPassword(),
            user.isEnabled(),
            true, true, true,
            getAuthorities(user)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return user.getUserRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.findByUserName(request.getUserName()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Create new user
        User user = User.builder()
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userRoles(request.getUserRoles())
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        // Generate JWT token
        UserDetails userDetails = loadUserByUsername(savedUser.getUserName());
        String token = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .user(savedUser)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName(),
                        request.getPassword()
                )
        );

        // Load user
        User user = userRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Generate JWT token
        UserDetails userDetails = loadUserByUsername(user.getUserName());
        String token = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .user(user)
                .build();
    }
}
