package com.artificialncool.authservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.artificialncool.authservice.model.Korisnik;

public interface UserRepository extends MongoRepository<Korisnik, String> {
    Optional<Korisnik> findByUsername(String username);
}
