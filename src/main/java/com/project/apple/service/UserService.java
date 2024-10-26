
package com.project.apple.service;

import com.project.apple.model.Role;
import com.project.apple.model.User;
import com.project.apple.responsitory.RoleRepository;
import com.project.apple.responsitory.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.security.Keys;


import java.security.Key;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${jwt.secret}")
    private String secretKey;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUserActiveStatus(Long id, boolean active) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Check if the user has the admin role with ID 2 and the request is to deactivate the account
            if (user.getRole().getId() == 2 && !active) {
                throw new IllegalArgumentException("Cannot deactivate an admin account");
            }

            user.setActive(active);
            userRepository.save(user);
            return user;
        } else {
            return null;  // Or throw an exception if the user is not found
        }
    }

    public User registerUser(User user) {
        if (user.getRole() != null && user.getRole().getId() == 2) {
            throw new IllegalArgumentException("Cannot register with admin role");
        }

        if (userRepository.existsByFullname(user.getFullname())) {
            throw new IllegalArgumentException("Fullname already exists");
        }

        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.setRole(defaultRole);
        return userRepository.save(user);
    }

    public String loginUser(String phoneNumber, String password, Long roleId) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with phone number: " + phoneNumber));
        if (!user.isActive()) {
            throw new BadCredentialsException("Tài khoản của " + user.getFullname() + " đã bị khóa, vui lòng liên hệ Admin!");
        }
        if (!user.getRole().getId().equals(roleId)) {
            throw new BadCredentialsException("Invalid role");
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            return generateToken(user.getPhoneNumber(), user.getRole().getId());
        } else {
            throw new BadCredentialsException("Invalid password");
        }
    }
    public String getUserRole(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with phone number: " + phoneNumber));
        return user.getRole().getName(); // Assuming Role has a getName() method
    }
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    private String generateToken(String username, Long roleId) {
        // Fetch user role name
        String role = userRepository.findByPhoneNumber(username)
                .map(user -> user.getRole().getName())
                .orElseThrow(() -> new UsernameNotFoundException("Role not found for user: " + username));

        System.out.println("Adding role to token: " + role);  // Debug statement

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)  // Ensure this matches the claim name used in extraction
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}