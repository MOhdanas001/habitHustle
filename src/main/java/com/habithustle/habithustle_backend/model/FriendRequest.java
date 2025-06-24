package com.habithustle.habithustle_backend.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "friendRequest")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FriendRequest {

    @Id
    private String id;

    @Indexed
    private String senderId;

    @Indexed
    private  String receiverId;

    @Indexed
    private String status; //PENDING ,ACCEPTED, REJECTED

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
