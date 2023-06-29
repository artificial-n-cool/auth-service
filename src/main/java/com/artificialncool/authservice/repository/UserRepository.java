package com.artificialncool.authservice.repository;

import com.artificialncool.authservice.model.Korisnik;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<Korisnik, String> {
    Optional<Korisnik> findByUsername(String username);
}
