package com.example.tweetapp.web;

import com.example.tweetapp.dto.TweetRequestTo;
import com.example.tweetapp.dto.TweetResponseTo;
import com.example.tweetapp.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tweets")
public class TweetController {
    private final TweetService service;

    public TweetController(TweetService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseTo create(@Valid @RequestBody TweetRequestTo req) { return service.create(req); }

    @GetMapping
    public List<TweetResponseTo> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public TweetResponseTo getById(@PathVariable Long id) { return service.getById(id); }

    @PutMapping("/{id}")
    public TweetResponseTo update(@PathVariable Long id, @Valid @RequestBody TweetRequestTo req) { return service.update(id, req); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}