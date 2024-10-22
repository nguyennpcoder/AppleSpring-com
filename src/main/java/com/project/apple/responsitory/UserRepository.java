package com.project.apple.responsitory;

import com.project.apple.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhoneNumber(String phoneNumber);
    boolean existsByFullname(String fullname);
    boolean existsByPhoneNumber(String phoneNumber);
}

