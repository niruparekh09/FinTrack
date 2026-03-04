package com.fintrack.api.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Checks if user exists by email or not.
     *
     * @param email
     * @return boolean
     */
    boolean existsByEmail(String email);
}
