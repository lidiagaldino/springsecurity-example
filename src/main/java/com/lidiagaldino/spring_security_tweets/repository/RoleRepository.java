package com.lidiagaldino.spring_security_tweets.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lidiagaldino.spring_security_tweets.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

  Role findByName(String name);
  
}
