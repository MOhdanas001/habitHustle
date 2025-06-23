package com.habithustle.habithustle_backend.controllers;

import com.habithustle.habithustle_backend.DTO.EmailReq;
import com.habithustle.habithustle_backend.DTO.LoginReq;
import com.habithustle.habithustle_backend.DTO.ResetPasswordreq;
import com.habithustle.habithustle_backend.model.PasswordResetToken;
import com.habithustle.habithustle_backend.model.User;
import com.habithustle.habithustle_backend.repository.PasswordResetRepository;
import com.habithustle.habithustle_backend.repository.UserRepository;
import com.habithustle.habithustle_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordResetRepository tokenRepo;
    @Autowired
    private JavaMailSender mailSender;

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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody EmailReq email) {
        try {
            System.out.println("email: " + email.getEmail());

            if (!userRepository.existsByEmail(email.getEmail())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "status", 0,
                        "message", "Email does not exist"
                ));
            }

            // Generate OTP
            String newToken = generateOTP();

            // Remove old OTPs
            tokenRepo.deleteByEmail(email.getEmail());

            // Save new token
            PasswordResetToken token = PasswordResetToken.builder()
                    .email(email.getEmail())
                    .token(newToken)
                    .expireAt(LocalDateTime.now().plusMinutes(30))
                    .build();

            tokenRepo.save(token);

            // Send email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email.getEmail());
            message.setSubject("Password Reset Request");
            message.setText("OTP to reset your password is: " + newToken);
            mailSender.send(message);

            return ResponseEntity.ok(Map.of(
                    "status", 1,
                    "message", "OTP sent successfully"
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 0,
                    "message", "Failed to send OTP. Please try again later."
            ));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordreq req) {
        try {
            Optional<User> userOpt = userRepository.findUserByEmail(req.getEmail());

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "status", 0,
                        "message", "Invalid email"
                ));
            }

            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(req.getPassword()));
            userRepository.save(user);

            // Remove OTP token after successful reset
            tokenRepo.deleteByEmail(user.getEmail());

            return ResponseEntity.ok(Map.of(
                    "status", 1,
                    "message", "Password reset successfully"
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 0,
                    "message", "Something went wrong during password reset"
            ));
        }
    }



public String generateOTP() {
    int otp = 10000 + new Random().nextInt(90000); // generates between 10000–99999
    return String.valueOf(otp);
}


}
