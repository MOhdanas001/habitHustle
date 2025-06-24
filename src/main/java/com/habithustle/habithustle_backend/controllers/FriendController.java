package com.habithustle.habithustle_backend.controllers;

import com.habithustle.habithustle_backend.DTO.RespndRequest;
import com.habithustle.habithustle_backend.model.User;
import com.habithustle.habithustle_backend.repository.UserRepository;
import com.habithustle.habithustle_backend.services.FriendRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    @Autowired
    private FriendRequestService service;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/send")
    public ResponseEntity<?> sendRequest(@RequestParam String toUserId, @AuthenticationPrincipal UserDetails user) {
        String senderEmail=user.getUsername();
        User sender = userRepository.findUserByEmail(senderEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Sender not found"));

        String senderId = sender.getId();
        String message = service.sendRequest(senderId, toUserId); // assuming username = userId
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/respond")
    public ResponseEntity<?> respondToRequest(
            @RequestBody RespndRequest req,
            @AuthenticationPrincipal UserDetails user) {

        String senderEmail=user.getUsername();
        User receiver = userRepository.findUserByEmail(senderEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Sender not found"));

        String receiverId = receiver.getId();
        String message = service.respondToRequest(req.getRequestId(), receiverId, req.getAccept());
        return ResponseEntity.ok(Map.of("message", message));
    }

    @GetMapping("/requests")
    public ResponseEntity<?> getPending(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getPendingRequests(user.getId()));
    }

    @GetMapping
    public ResponseEntity<?> getFriends(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getFriends(user.getId()));
    }
}

