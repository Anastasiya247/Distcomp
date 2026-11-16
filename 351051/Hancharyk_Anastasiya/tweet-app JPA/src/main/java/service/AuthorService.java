package com.example.tweetapp.service;

import com.example.tweetapp.dto.*;
import com.example.tweetapp.exception.ApiException;
import com.example.tweetapp.model.Author;
import com.example.tweetapp.repository.AuthorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {
    private final AuthorRepository repo;

    public AuthorService(AuthorRepository repo) { this.repo = repo; }

    @Transactional
    public AuthorResponseTo create(AuthorRequestTo req) {
        if (repo.findByLogin(req.getLogin()).isPresent()) {
            throw new ApiException(HttpStatus.FORBIDDEN, 40301, "Login already exists");
        }
        Author a = new Author();
        a.setLogin(req.getLogin());
        a.setPassword(req.getPassword());
        a.setFirstname(req.getFirstname());
        a.setLastname(req.getLastname());
        return toDto(repo.save(a));
    }

    public List<AuthorResponseTo> getAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public AuthorResponseTo getById(Long id) {
        return repo.findById(id).map(this::toDto).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 40401, "Author not found"));
    }

    @Transactional
    public AuthorResponseTo update(Long id, AuthorRequestTo req) {
        Author a = repo.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 40401, "Author not found"));
        a.setLogin(req.getLogin());
        a.setPassword(req.getPassword());
        a.setFirstname(req.getFirstname());
        a.setLastname(req.getLastname());
        return toDto(repo.save(a));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ApiException(HttpStatus.NOT_FOUND, 40401, "Author not found");
        repo.deleteById(id);
    }

    private AuthorResponseTo toDto(Author a) {
        AuthorResponseTo dto = new AuthorResponseTo();
        dto.setId(a.getId());
        dto.setLogin(a.getLogin());
        dto.setFirstname(a.getFirstname());
        dto.setLastname(a.getLastname());
        return dto;
    }
}