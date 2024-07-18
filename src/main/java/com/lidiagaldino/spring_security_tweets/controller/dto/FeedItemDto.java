package com.lidiagaldino.spring_security_tweets.controller.dto;

public record FeedItemDto(Long tweetId, String content, String username) {
  
}
