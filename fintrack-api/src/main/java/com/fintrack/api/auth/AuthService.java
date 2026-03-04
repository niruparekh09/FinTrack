package com.fintrack.api.auth;

import com.fintrack.api.exception.UserAlreadyExistsException;
import com.fintrack.api.user.User;
import com.fintrack.api.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final AuthMapper mapper;


    AuthService(UserRepository repo, PasswordEncoder encoder, AuthMapper mapper) {
        this.repository = repo;
        this.encoder = encoder;
        this.mapper = mapper;
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
        return new AuthResponse();
    }
}
