package com.lidiagaldino.spring_security_tweets.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.lidiagaldino.spring_security_tweets.controller.dto.CreateTweetDto;
import com.lidiagaldino.spring_security_tweets.controller.dto.FeedDto;
import com.lidiagaldino.spring_security_tweets.controller.dto.FeedItemDto;
import com.lidiagaldino.spring_security_tweets.entities.Tweet;
import com.lidiagaldino.spring_security_tweets.repository.TweetRepository;
import com.lidiagaldino.spring_security_tweets.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class TweetController {
  private final TweetRepository tweetRepository;
  private final UserRepository userRepository;

  public TweetController(TweetRepository tweetRepository, UserRepository userRepository) {
    this.tweetRepository = tweetRepository;
    this.userRepository = userRepository;
  }

  @PostMapping("/tweets")
  public ResponseEntity<Void> store(
    @RequestBody CreateTweetDto createTweetDto, 
    JwtAuthenticationToken authToken
    ) {
      var user = userRepository.findById(UUID.fromString(authToken.getName()));
      var tweet = new Tweet();
      tweet.setUser(user.get());
      tweet.setContent(createTweetDto.content());

      tweetRepository.save(tweet);
      return ResponseEntity.ok().build();
  }

  @DeleteMapping("/tweets/{id}")
  public ResponseEntity<Void> remove(@PathVariable("id") Long tweetId, JwtAuthenticationToken token){
    var user = userRepository.findById(UUID.fromString(token.getName()));
    var tweet = tweetRepository.findById(tweetId).orElseThrow(
      () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
    );

    if(!tweet.getUser().getUserId().equals(user.get().getUserId())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    tweetRepository.deleteById(tweetId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/feed")
  public ResponseEntity<FeedDto> index(
    @RequestParam(value =  "page", defaultValue = "0") int page,
    @RequestParam(value = "size", defaultValue = "10") int size
  ) {
    var tweets = tweetRepository.findAll(
      PageRequest.of(page, size, Sort.Direction.DESC, "creationTimestamp")
    ).map(
      tweet -> new FeedItemDto(tweet.getTweetId(), tweet.getContent(), tweet.getUser().getUsername())
    );

    return ResponseEntity.ok(
      new FeedDto(
        tweets.getContent(), 
        page, 
        size, 
        tweets.getTotalPages(), 
        tweets.getTotalElements()
      )
      );
  }
}
