package com.habithustle.habithustle_backend.repository;

import com.habithustle.habithustle_backend.model.Hustle;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface HustleRepository extends MongoRepository<Hustle,String> {


}
