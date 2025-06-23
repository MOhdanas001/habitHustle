package com.habithustle.habithustle_backend.repository;

import com.habithustle.habithustle_backend.model.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PasswordResetRepository extends MongoRepository<PasswordResetToken,String> {

    Optional<PasswordResetToken> findByToken(String token);
    void deleteByEmail(String email);

}
