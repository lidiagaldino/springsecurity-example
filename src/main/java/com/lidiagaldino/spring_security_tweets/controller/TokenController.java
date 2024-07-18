package com.lidiagaldino.spring_security_tweets.controller;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.RestController;

import com.lidiagaldino.spring_security_tweets.controller.dto.LoginRequest;
import com.lidiagaldino.spring_security_tweets.controller.dto.LoginResponse;
import com.lidiagaldino.spring_security_tweets.entities.Role;
import com.lidiagaldino.spring_security_tweets.repository.UserRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class TokenController {
  private final JwtEncoder jwtEncoder;
  private final UserRepository userRepository; 
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public TokenController(
    JwtEncoder jwtEncoder, 
    UserRepository userRepository, 
    BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
    this.jwtEncoder = jwtEncoder;
    this.userRepository = userRepository; 
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  @PostMapping("login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
      var user = userRepository.findByUsername(req.username());
      if(user.isEmpty() || !user.get().isLoginCorrect(req, bCryptPasswordEncoder)){
        throw new BadCredentialsException("user or password is invalid");
      }

      var now = Instant.now();
      var expiresIn = 3000L;

      var scopes = user.get().getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));
      var claims = JwtClaimsSet.builder()
                .issuer("mybackend")
                .subject(user.get().getUserId().toString())
                .expiresAt(now.plusSeconds(expiresIn))
                .issuedAt(now)
                .claim("scope", scopes)
                .build();

      var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
      return ResponseEntity.ok(new LoginResponse(jwtValue, expiresIn));
  } 
}
