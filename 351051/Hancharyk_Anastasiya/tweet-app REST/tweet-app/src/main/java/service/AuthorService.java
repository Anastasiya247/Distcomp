package com.example.tweetapp.service;

import com.example.tweetapp.dto.AuthorRequestTo;
import com.example.tweetapp.dto.AuthorResponseTo;
import com.example.tweetapp.exception.ApiException;
import com.example.tweetapp.model.Author;
import com.example.tweetapp.model.Tweet;
import com.example.tweetapp.repository.AuthorRepository;
import com.example.tweetapp.repository.TweetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {
    private final AuthorRepository repo;
    private final TweetRepository tweetRepository; // Добавили репозиторий твитов

    public AuthorService(AuthorRepository repo, TweetRepository tweetRepository) {
        this.repo = repo;
        this.tweetRepository = tweetRepository;
    }

    public AuthorResponseTo create(AuthorRequestTo req) {
        if (repo.findByLogin(req.getLogin()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, 40901, "Login already exists");
        }
        Author author = new Author();
        author.setLogin(req.getLogin());
        author.setPassword(req.getPassword());
        author.setFirstname(req.getFirstname());
        author.setLastname(req.getLastname());
        return toDto(repo.save(author));
    }

    public List<AuthorResponseTo> getAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public AuthorResponseTo getById(Long id) {
        return toDto(repo.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 40401, "Author not found")));
    }

    // НОВЫЙ МЕТОД: Получить автора по ID твита
    public AuthorResponseTo getAuthorByTweetId(Long tweetId) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 40405, "Tweet not found"));
        return getById(tweet.getAuthorId());
    }

    public AuthorResponseTo update(Long id, AuthorRequestTo req) {
        Author author = repo.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 40401, "Author not found"));
        repo.findByLogin(req.getLogin()).ifPresent(a -> {
            if (!a.getId().equals(id)) throw new ApiException(HttpStatus.CONFLICT, 40901, "Login already exists");
        });
        author.setLogin(req.getLogin());
        author.setPassword(req.getPassword());
        author.setFirstname(req.getFirstname());
        author.setLastname(req.getLastname());
        return toDto(repo.save(author));
    }

    public void delete(Long id) {
        if (!repo.deleteById(id)) throw new ApiException(HttpStatus.NOT_FOUND, 40401, "Author not found");
    }

    private AuthorResponseTo toDto(Author author) {
        AuthorResponseTo dto = new AuthorResponseTo();
        dto.setId(author.getId());
        dto.setLogin(author.getLogin());
        dto.setFirstname(author.getFirstname());
        dto.setLastname(author.getLastname());
        return dto;
    }
}