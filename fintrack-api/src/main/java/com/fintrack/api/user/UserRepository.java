package com.fintrack.api.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Checks if user exists by email or not.
     *
     * @param email
     * @return boolean
     */
    boolean existsByEmail(String email);

    /**
     * Finds the user ny email
     *
     * @param email
     * @return user
     */
    Optional<User> findByEmail(String email);

}
