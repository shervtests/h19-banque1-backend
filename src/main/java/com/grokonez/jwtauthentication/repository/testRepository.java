package com.grokonez.jwtauthentication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grokonez.jwtauthentication.model.User;


public interface testRepository extends JpaRepository<User, String> {
	 public User findByUsername(String username);
	 public Optional<User> findById(String id);
	 
}