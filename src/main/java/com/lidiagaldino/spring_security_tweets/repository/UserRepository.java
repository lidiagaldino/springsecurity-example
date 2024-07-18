package com.lidiagaldino.spring_security_tweets.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lidiagaldino.spring_security_tweets.entities.User;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByUsername(String username);
  
}
