package com.example.TheMovieTracker.repositories;

import com.example.TheMovieTracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);  // Find user by username
    User findByEmail(String email);  // Find user by email
}
