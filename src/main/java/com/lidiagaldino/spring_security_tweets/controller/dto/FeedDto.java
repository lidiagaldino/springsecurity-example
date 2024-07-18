package com.lidiagaldino.spring_security_tweets.controller.dto;

import java.util.List;

public record FeedDto(
  List<FeedItemDto> feedItems,
  int page,
  int size,
  int totalElements,
  Long totalPages
) {}
