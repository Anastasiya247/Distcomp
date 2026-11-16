package com.example.tweetapp.web;

import com.example.tweetapp.dto.AuthorRequestTo;
import com.example.tweetapp.dto.AuthorResponseTo;
import com.example.tweetapp.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/authors")
public class AuthorController {
    private final AuthorService service;

    public AuthorController(AuthorService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponseTo create(@Valid @RequestBody AuthorRequestTo req) { return service.create(req); }

    @GetMapping
    public List<AuthorResponseTo> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public AuthorResponseTo getById(@PathVariable Long id) { return service.getById(id); }

    @PutMapping("/{id}")
    public AuthorResponseTo update(@PathVariable Long id, @Valid @RequestBody AuthorRequestTo req) { return service.update(id, req); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}