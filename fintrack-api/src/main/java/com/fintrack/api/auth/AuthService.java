package com.fintrack.api.auth;

import com.fintrack.api.exception.UserAlreadyExistsException;
import com.fintrack.api.exception.UserNotFoundException;
import com.fintrack.api.security.JwtUtil;
import com.fintrack.api.security.UserDetailsServiceImpl;
import com.fintrack.api.user.User;
import com.fintrack.api.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final AuthMapper mapper;
    private final UserDetailsServiceImpl userDetailService;
    private final JwtUtil jwtUtil;


    AuthService(UserRepository repo, PasswordEncoder encoder, AuthMapper mapper, UserDetailsServiceImpl userDetailService, JwtUtil util) {
        this.repository = repo;
        this.encoder = encoder;
        this.mapper = mapper;
        this.userDetailService = userDetailService;
        this.jwtUtil = util;
    }

    // Registers new users. Follows SRP
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        if (repository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email Already Registered");
        }

        String encodedPassword = encoder.encode(request.getPassword());

        User newUser = mapper.toUser(request, encodedPassword);

        User savedUser = repository.save(newUser);

        return new RegisterResponse(
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()
        );
    }

    public AuthResponse login(AuthRequest request) {

        User user = repository.findByEmail(request.getEmail()).orElseThrow(
                () -> new UserNotFoundException("Invalid Credentials")
        );

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid Credentials");
        }

        UserDetails userDetails = userDetailService.loadUserByUsername(user.getEmail());

        String token = jwtUtil.generateToken(userDetails, user.getId());

        return new AuthResponse(
                token,
                LocalDateTime.now()
        );
    }
}
