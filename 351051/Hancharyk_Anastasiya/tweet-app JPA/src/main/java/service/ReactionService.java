package com.example.tweetapp.service;

import com.example.tweetapp.dto.*;
import com.example.tweetapp.exception.ApiException;
import com.example.tweetapp.model.Reaction;
import com.example.tweetapp.repository.ReactionRepository;
import com.example.tweetapp.repository.TweetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReactionService {
    private final ReactionRepository repo;
    private final TweetRepository tweetRepo;

    public ReactionService(ReactionRepository repo, TweetRepository tweetRepo) { this.repo = repo; this.tweetRepo = tweetRepo; }

    @Transactional
    public ReactionResponseTo create(ReactionRequestTo req) {
        var tweet = tweetRepo.findById(req.getTweetId()).orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, 40304, "Tweet not found"));
        Reaction r = new Reaction(); r.setTweet(tweet); r.setContent(req.getContent());
        return toDto(repo.save(r));
    }

    public List<ReactionResponseTo> getAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public ReactionResponseTo getById(Long id) {
        return repo.findById(id).map(this::toDto).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 40404, "Reaction not found"));
    }

    @Transactional
    public ReactionResponseTo update(Long id, ReactionRequestTo req) {
        Reaction r = repo.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 40404, "Reaction not found"));
        var tweet = tweetRepo.findById(req.getTweetId()).orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, 40304, "Tweet not found"));
        r.setTweet(tweet); r.setContent(req.getContent());
        return toDto(repo.save(r));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ApiException(HttpStatus.NOT_FOUND, 40404, "Reaction not found");
        repo.deleteById(id);
    }

    public List<ReactionResponseTo> getByTweetId(Long tweetId) {
        return repo.findByTweetId(tweetId).stream().map(this::toDto).collect(Collectors.toList());
    }

    private ReactionResponseTo toDto(Reaction r) {
        ReactionResponseTo dto = new ReactionResponseTo();
        dto.setId(r.getId()); dto.setTweetId(r.getTweet().getId()); dto.setContent(r.getContent());
        return dto;
    }
}