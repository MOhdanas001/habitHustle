package com.habithustle.habithustle_backend.repository;

import com.habithustle.habithustle_backend.model.FriendRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends MongoRepository<FriendRequest,String> {

    boolean existsBySenderIdAndReceiverIdAndStatus(String senderId, String receiverId, String status);
    List<FriendRequest> findByReceiverIdAndStatus(String receiverId, String status);
    Optional<FriendRequest> findByIdAndReceiverId(String id, String receiverId);
}
