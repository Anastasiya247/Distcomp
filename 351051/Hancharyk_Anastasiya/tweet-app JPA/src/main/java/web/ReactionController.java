package com.example.tweetapp.web;

import com.example.tweetapp.dto.ReactionRequestTo;
import com.example.tweetapp.dto.ReactionResponseTo;
import com.example.tweetapp.service.ReactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/reactions")
public class ReactionController {
    private final ReactionService service;

    public ReactionController(ReactionService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReactionResponseTo create(@Valid @RequestBody ReactionRequestTo req) { return service.create(req); }

    @GetMapping
    public List<ReactionResponseTo> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public ReactionResponseTo getById(@PathVariable Long id) { return service.getById(id); }

    @PutMapping("/{id}")
    public ReactionResponseTo update(@PathVariable Long id, @Valid @RequestBody ReactionRequestTo req) { return service.update(id, req); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }

    @GetMapping("/by-tweet/{tweetId}")
    public List<ReactionResponseTo> getByTweetId(@PathVariable Long tweetId) { return service.getByTweetId(tweetId); }
}