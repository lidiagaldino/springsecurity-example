package com.lidiagaldino.spring_security_tweets.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lidiagaldino.spring_security_tweets.entities.Tweet;

public interface TweetRepository extends JpaRepository<Tweet, Long>{
  
}
