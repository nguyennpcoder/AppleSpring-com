
package com.project.apple.controller;

import com.project.apple.dto.UserDTO;
import com.project.apple.model.User;
import com.project.apple.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDTO userDTO) {
        String token = userService.loginUser(userDTO.getPhoneNumber(), userDTO.getPassword(), userDTO.getRoleId());
        String role = userService.getUserRole(userDTO.getPhoneNumber());

        return ResponseEntity.ok(new HashMap<String, String>() {{
            put("role", role);
            put("message", "Login success.");

            put("token", token);
        }});
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return userService.saveUser(user);
    }

    @PatchMapping("/{id}/{active}")
    public ResponseEntity<?> updateUserActiveStatus(@PathVariable Long id, @PathVariable boolean active) {
        try {
            User updatedUser = userService.updateUserActiveStatus(id, active);
            if (updatedUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng tồn tại!");
            }
            String statusMessage = active ? "Đã mở khóa người dùng " : "Đã khóa người dùng ";
            String message = statusMessage + updatedUser.getFullname() + " thành công!";

            Map<String, Object> response = new HashMap<>();
            response.put("user", updatedUser);
            response.put("message", message);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}