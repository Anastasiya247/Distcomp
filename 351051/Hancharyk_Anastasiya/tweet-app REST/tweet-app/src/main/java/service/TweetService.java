package com.example.tweetapp.service;

import com.example.tweetapp.dto.TweetRequestTo;
import com.example.tweetapp.dto.TweetResponseTo;
import com.example.tweetapp.exception.ApiException;
import com.example.tweetapp.model.Tweet;
import com.example.tweetapp.repository.AuthorRepository;
import com.example.tweetapp.repository.LabelRepository;
import com.example.tweetapp.repository.TweetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TweetService {
    private final TweetRepository repo;
    private final AuthorRepository authorRepo;
    private final LabelRepository labelRepo;

    public TweetService(TweetRepository repo, AuthorRepository authorRepo, LabelRepository labelRepo) {
        this.repo = repo;
        this.authorRepo = authorRepo;
        this.labelRepo = labelRepo;
    }

    public TweetResponseTo create(TweetRequestTo req) {
        if (!authorRepo.existsById(req.getAuthorId())) throw new ApiException(HttpStatus.BAD_REQUEST, 40002, "Author not found");
        if (req.getLabelIds() != null) {
            for (Long labelId : req.getLabelIds()) {
                if (!labelRepo.existsById(labelId)) throw new ApiException(HttpStatus.BAD_REQUEST, 40003, "Label not found");
            }
        }
        Tweet tweet = new Tweet();
        tweet.setAuthorId(req.getAuthorId());
        tweet.setTitle(req.getTitle());
        tweet.setContent(req.getContent());
        tweet.setLabelIds(req.getLabelIds() != null ? new HashSet<>(req.getLabelIds()) : new HashSet<>());
        tweet.setCreated(Instant.now());
        tweet.setModified(Instant.now());
        return toDto(repo.save(tweet));
    }

    public List<TweetResponseTo> getAll() { return repo.findAll().stream().map(this::toDto).collect(Collectors.toList()); }

    public TweetResponseTo getById(Long id) {
        return toDto(repo.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 40403, "Tweet not found")));
    }

    public TweetResponseTo update(Long id, TweetRequestTo req) {
        Tweet tweet = repo.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 40403, "Tweet not found"));
        if (!authorRepo.existsById(req.getAuthorId())) throw new ApiException(HttpStatus.BAD_REQUEST, 40002, "Author not found");
        if (req.getLabelIds() != null) {
            for (Long labelId : req.getLabelIds()) {
                if (!labelRepo.existsById(labelId)) throw new ApiException(HttpStatus.BAD_REQUEST, 40003, "Label not found");
            }
        }
        tweet.setAuthorId(req.getAuthorId());
        tweet.setTitle(req.getTitle());
        tweet.setContent(req.getContent());
        tweet.setLabelIds(req.getLabelIds() != null ? new HashSet<>(req.getLabelIds()) : new HashSet<>());
        tweet.setModified(Instant.now());
        return toDto(repo.save(tweet));
    }

    public void delete(Long id) {
        if (!repo.deleteById(id)) throw new ApiException(HttpStatus.NOT_FOUND, 40403, "Tweet not found");
    }

    private TweetResponseTo toDto(Tweet tweet) {
        TweetResponseTo dto = new TweetResponseTo();
        dto.setId(tweet.getId());
        dto.setAuthorId(tweet.getAuthorId());
        dto.setTitle(tweet.getTitle());
        dto.setContent(tweet.getContent());
        dto.setCreated(tweet.getCreated());
        dto.setModified(tweet.getModified());
        dto.setLabelIds(tweet.getLabelIds());
        return dto;
    }
}
