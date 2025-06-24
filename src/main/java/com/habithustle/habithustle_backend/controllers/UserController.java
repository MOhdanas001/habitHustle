package com.habithustle.habithustle_backend.controllers;

import com.habithustle.habithustle_backend.DTO.SearchRequest;
import com.habithustle.habithustle_backend.DTO.SearchResponse;
import com.habithustle.habithustle_backend.model.User;
import com.habithustle.habithustle_backend.repository.UserRepository;
import com.habithustle.habithustle_backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @GetMapping("/search/user")
    public ResponseEntity<?> searchUser(@RequestParam("val") String value) {
        try {

            List<User> users = userRepository.searchByUsername(value);
            List<SearchResponse> result = users.stream()
                    .map(user -> new SearchResponse(
                            user.getId(),
                            user.getUsername(),
                            user.getProfileURL()
                    ))
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "status", 1,
                    "data", result

            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 0,
                    "message", "An error occurred while searching for users"
            ));
        }
    }



}
