package com.habithustle.habithustle_backend.services;

import com.habithustle.habithustle_backend.DTO.SearchResponse;
import com.habithustle.habithustle_backend.model.FriendRequest;
import com.habithustle.habithustle_backend.model.User;
import com.habithustle.habithustle_backend.repository.FriendRequestRepository;
import com.habithustle.habithustle_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FriendRequestService {

    @Autowired
    private FriendRequestRepository friendRequestRepo;
    @Autowired
    private UserRepository userRepo;


    public String sendRequest(String senderId, String receiverId) {
        if (senderId.equals(receiverId)) return "Cannot send friend request to yourself.";

        if (friendRequestRepo.existsBySenderIdAndReceiverIdAndStatus(senderId, receiverId, "PENDING")) {
            return "Request already pending.";
        }

        friendRequestRepo.save(FriendRequest.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        return "Friend request sent.";
    }

    public String respondToRequest(String requestId, String receiverId, boolean accept) {
        System.out.println("rId: "+ receiverId);
        System.out.println("rqId: "+ requestId);
        FriendRequest request = friendRequestRepo.findByIdAndReceiverId(requestId, receiverId)
                .orElseThrow(() -> new RuntimeException("Request not found."));

        if (!"PENDING".equals(request.getStatus())) return "Request already responded.";

        request.setStatus(accept ? "ACCEPTED" : "REJECTED");
        friendRequestRepo.save(request);

        if (accept) {
            addFriendBothWays(request.getSenderId(), request.getReceiverId());
            return "Friend request accepted.";
        }

        return "Friend request rejected.";
    }

    public List<FriendRequest> getPendingRequests(String receiverId) {
        return friendRequestRepo.findByReceiverIdAndStatus(receiverId, "PENDING");
    }

    public List<SearchResponse> getFriends(String userId) {
        return userRepo.findById(userId)
                .map(user -> userRepo.findAllById(user.getFriends()).stream()
                        .map(u -> new SearchResponse(u.getId(), u.getUsername(), u.getProfileURL()))
                        .toList()
                )
                .orElse(List.of());
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    public void addFriendBothWays(String userAId, String userBId) {

        System.out.print("Addind Two way friends");
        // Ensure no null or same ID friendship
        if (userAId == null || userBId == null || userAId.equals(userBId)) {
            throw new IllegalArgumentException("User IDs must be non-null and different.");
        }

        // Add B to A's friend list
        Query queryA = new Query(Criteria.where("_id").is(userAId));
        Update updateA = new Update().addToSet("friends", userBId);
        mongoTemplate.updateFirst(queryA, updateA, User.class);

        // Add A to B's friend list
        Query queryB = new Query(Criteria.where("_id").is(userBId));
        Update updateB = new Update().addToSet("friends", userAId);
        mongoTemplate.updateFirst(queryB, updateB, User.class);
    }
}
