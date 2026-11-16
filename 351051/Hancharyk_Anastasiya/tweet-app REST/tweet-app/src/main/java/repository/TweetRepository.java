package com.example.tweetapp.repository;

import com.example.tweetapp.model.Tweet;
import org.springframework.stereotype.Repository;

@Repository
public class TweetRepository extends InMemoryRepository<Tweet> {
}
