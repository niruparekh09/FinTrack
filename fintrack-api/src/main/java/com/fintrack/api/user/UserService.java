package com.fintrack.api.user;

import com.fintrack.api.exception.UserNotFoundException;
import com.fintrack.api.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserResponse getUser() {
        String email = SecurityUtils.getCurrentUsername();

        User user = repository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User with this email not registered"));

        return mapper.toResponse(user);
    }
}
