package com.habithustle.habithustle_backend.controllers;

import com.habithustle.habithustle_backend.DTO.LoginReq;
import com.habithustle.habithustle_backend.model.User;
import com.habithustle.habithustle_backend.repository.UserRepository;
import com.habithustle.habithustle_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<?> RegisterUser(@RequestBody User user) {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(user.getEmail())) {
                return ResponseEntity.ok(Map.of(
                        "status", 0,
                        "message", "Email already exists"
                ));
            }

            // Check if username already exists
            if (userRepository.existsByUsername(user.getUsername())) {
                return ResponseEntity.ok(Map.of(
                        "status", 0,
                        "message", "Username already exists"
                ));
            }

            // Save new user
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole("User");

            User savedUser = userRepository.save(user);
            String token = jwtUtil.generateToken(savedUser);

            return ResponseEntity.ok(Map.of(
                    "status", 1,
                    "token", token,
                    "username", savedUser.getUsername(),
                    "email", savedUser.getEmail()
            ));
        } catch (Exception e) {
            e.printStackTrace(); // You can also log this
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 0,
                    "message", "An error occurred during registration"
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody LoginReq request){
try {
    Optional<User> user;
    // Try finding by email
    if (request.getIdentifier().contains("@")) {
        user = userRepository.findUserByEmail(request.getIdentifier());
    } else {
        user = userRepository.findByUsername(request.getIdentifier());
    }

    if (user.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "status", 0,
                "message", "Invalid username/email or password"
        ));
    }
    if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "status", -1,
                "message", "Invalid Credentials"

        ));
    }

    String token = jwtUtil.generateToken(user.get());

    return ResponseEntity.ok(Map.of(
            "status", 1,
            "token", token,
            "username", user.get().getUsername(),
            "email", user.get().getEmail()
    ));
} catch (Exception e) {
    e.printStackTrace();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "status", 0,
            "message", "Login failed due to internal error"
    ));
}


    }




}
