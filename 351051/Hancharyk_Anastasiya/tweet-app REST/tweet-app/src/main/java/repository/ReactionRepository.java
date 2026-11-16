package com.example.tweetapp.repository;

import com.example.tweetapp.model.Reaction;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ReactionRepository extends InMemoryRepository<Reaction> {
    // Ищем все реакции, которые относятся к конкретному твиту
    public List<Reaction> findByTweetId(Long tweetId) {
        return storage.values().stream()
                .filter(r -> r.getTweetId().equals(tweetId))
                .collect(Collectors.toList());
    }
}