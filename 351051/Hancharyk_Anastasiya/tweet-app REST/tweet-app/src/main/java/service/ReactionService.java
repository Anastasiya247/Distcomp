package com.example.tweetapp.service;

import com.example.tweetapp.dto.ReactionRequestTo;
import com.example.tweetapp.dto.ReactionResponseTo;
import com.example.tweetapp.exception.ApiException;
import com.example.tweetapp.model.Reaction;
import com.example.tweetapp.repository.ReactionRepository;
import com.example.tweetapp.repository.TweetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReactionService {
    private final ReactionRepository repo;
    private final TweetRepository tweetRepo;

    public ReactionService(ReactionRepository repo, TweetRepository tweetRepo) {
        this.repo = repo;
        this.tweetRepo = tweetRepo;
    }

    public ReactionResponseTo create(ReactionRequestTo req) {
        if (!tweetRepo.existsById(req.getTweetId())) throw new ApiException(HttpStatus.BAD_REQUEST, 40004, "Tweet not found");
        Reaction reaction = new Reaction();
        reaction.setTweetId(req.getTweetId());
        reaction.setContent(req.getContent());
        return toDto(repo.save(reaction));
    }

    public List<ReactionResponseTo> getAll() { return repo.findAll().stream().map(this::toDto).collect(Collectors.toList()); }

    public ReactionResponseTo getById(Long id) {
        return toDto(repo.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 40404, "Reaction not found")));
    }

    // НОВЫЙ МЕТОД: Получить реакции по ID твита
    public List<ReactionResponseTo> getReactionsByTweetId(Long tweetId) {
        if (!tweetRepo.existsById(tweetId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, 40405, "Tweet not found");
        }
        return repo.findByTweetId(tweetId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ReactionResponseTo update(Long id, ReactionRequestTo req) {
        Reaction reaction = repo.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 40404, "Reaction not found"));
        if (!tweetRepo.existsById(req.getTweetId())) throw new ApiException(HttpStatus.BAD_REQUEST, 40004, "Tweet not found");
        reaction.setTweetId(req.getTweetId());
        reaction.setContent(req.getContent());
        return toDto(repo.save(reaction));
    }

    public void delete(Long id) {
        if (!repo.deleteById(id)) throw new ApiException(HttpStatus.NOT_FOUND, 40404, "Reaction not found");
    }

    private ReactionResponseTo toDto(Reaction reaction) {
        ReactionResponseTo dto = new ReactionResponseTo();
        dto.setId(reaction.getId());
        dto.setTweetId(reaction.getTweetId());
        dto.setContent(reaction.getContent());
        return dto;
    }
}