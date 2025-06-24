package com.habithustle.habithustle_backend.controllers;

import com.habithustle.habithustle_backend.DTO.EmailReq;
import com.habithustle.habithustle_backend.DTO.LoginReq;
import com.habithustle.habithustle_backend.DTO.ResetPasswordreq;
import com.habithustle.habithustle_backend.DTO.UserRegistrationReq;
import com.habithustle.habithustle_backend.model.PasswordResetToken;
import com.habithustle.habithustle_backend.model.User;
import com.habithustle.habithustle_backend.repository.PasswordResetRepository;
import com.habithustle.habithustle_backend.repository.UserRepository;
import com.habithustle.habithustle_backend.services.ImagekitService;
import com.habithustle.habithustle_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @Autowired
    private ImagekitService imagekitService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestPart UserRegistrationReq userDto,
            @RequestPart MultipartFile imageFile) {

        try {
            // 1. Check for email or username already taken
            if (userRepository.existsByEmail(userDto.getEmail())) {
                return ResponseEntity.ok(Map.of("status", 0, "message", "Email already exists"));
            }

            if (userRepository.existsByUsername(userDto.getUsername())) {
                return ResponseEntity.ok(Map.of("status", 0, "message", "Username already exists"));
            }

            // 2. Upload the image and get resized CDN URL
            String imageUrl = imagekitService.uploadProfile(imageFile);

            // 3. Create actual User object from DTO
            User user = new User();
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            user.setUsername(userDto.getUsername());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.setRole("User");
            user.setProfileURL(imageUrl); // <-- set the uploaded image URL

            // 4. Save user to DB
            User savedUser = userRepository.save(user);

            // 5. Generate JWT token
            String token = jwtUtil.generateToken(savedUser);

            return ResponseEntity.ok(Map.of(
                    "status", 1,
                    "message", "User registered successfully",
                    "token", token,
                    "username", savedUser.getUsername(),
                    "email", savedUser.getEmail()

            ));

        } catch (Exception e) {
            e.printStackTrace();
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
