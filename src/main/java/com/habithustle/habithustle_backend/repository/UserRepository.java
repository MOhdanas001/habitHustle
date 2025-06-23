package com.habithustle.habithustle_backend.repository;

import com.habithustle.habithustle_backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface UserRepository extends MongoRepository<User,String> {
     Optional<User> findUserByEmail(String email);
     Boolean existsByEmail(String email);
     Boolean existsByUsername(String username);
     Optional<User> findByUsername(String username);
}
