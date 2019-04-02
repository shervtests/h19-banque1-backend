package com.grokonez.jwtauthentication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grokonez.jwtauthentication.model.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface UserRepository extends JpaRepository<User, Long> ,JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByQuestion1(String question1);
    Boolean existsByAnswer1(String answer1);
    Boolean existsByQuestion2(String question2);
    Boolean existsByAnswer2(String answer2);
    Boolean existsByFirstnameAndLastname(String FirstName, String Lastname);
    
}