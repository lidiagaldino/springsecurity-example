package com.lidiagaldino.spring_security_tweets.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.lidiagaldino.spring_security_tweets.controller.dto.CreateUserDto;
import com.lidiagaldino.spring_security_tweets.entities.Role;
import com.lidiagaldino.spring_security_tweets.entities.User;
import com.lidiagaldino.spring_security_tweets.repository.RoleRepository;
import com.lidiagaldino.spring_security_tweets.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



@RestController 
public class UserController {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  
  public UserController(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEnc) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEnc;
  }

  @PostMapping("/users")
  @Transactional
  public ResponseEntity<CreateUserDto> store(@RequestBody CreateUserDto dto) {
    var basicRole = roleRepository.findByName(Role.Values.BASIC.name());

    var userFromDb = userRepository.findByUsername(dto.username());
    if(userFromDb.isPresent()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    var user = new User();
    user.setUsername(dto.username());
    user.setPassword(passwordEncoder.encode(dto.password()));
    user.setRoles(Set.of(basicRole));

    userRepository.save(user);
    return ResponseEntity.ok().build();
  }
  
  @GetMapping("/users")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<List<User>> index() {
      var users = userRepository.findAll();
      return ResponseEntity.ok(users);
  } 
}
