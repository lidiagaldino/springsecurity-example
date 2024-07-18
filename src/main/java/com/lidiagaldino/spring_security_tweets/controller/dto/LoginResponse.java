package com.lidiagaldino.spring_security_tweets.controller.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
  
}
