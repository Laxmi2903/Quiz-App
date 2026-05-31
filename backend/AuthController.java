package com.quizapp.controller;

import com.quizapp.dto.AuthRequest;
import com.quizapp.dto.AuthResponse;
import com.quizapp.model.User;
import com.quizapp.repository.UserRepository;
import com.quizapp.security.JwtUtil;
import com.quizapp.util.AppConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * POST /api/auth/register
     * Body: { "username": "...", "password": "...", "email": "..." }
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername())
                || userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(AppConstants.USER_ALREADY_EXISTS);
        }

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                AppConstants.ROLE_USER   // default role; promote to ROLE_ADMIN manually/DB
        );
        userRepository.save(user);
        return ResponseEntity.ok(AppConstants.USER_REGISTERED_SUCCESS);
    }

    /**
     * POST /api/auth/login
     * Body: { "username": "...", "password": "..." }
     * Returns JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getRole()));
    }
}
