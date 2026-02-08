package com.courseplatform.service;

import com.courseplatform.dto.request.LoginRequest;
import com.courseplatform.dto.request.RegisterRequest;
import com.courseplatform.dto.response.AuthResponse;
import com.courseplatform.dto.response.RegisterResponse;
import com.courseplatform.entity.User;
import com.courseplatform.exception.ConflictException;
import com.courseplatform.repository.UserRepository;
import com.courseplatform.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered");
        }
        
        // Create new user
        Set<String> roles = new HashSet<>();
        roles.add("USER"); // User entity adds "ROLE_" prefix in getAuthorities()
        
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build();
        
        user = userRepository.save(user);
        
        return RegisterResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .message("User registered successfully")
                .build();
    }
    
    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        // Generate JWT token
        String token = jwtUtil.generateToken(request.getEmail());
        
        return AuthResponse.builder()
                .token(token)
                .email(request.getEmail())
                .expiresIn(jwtExpiration)
                .build();
    }
}
