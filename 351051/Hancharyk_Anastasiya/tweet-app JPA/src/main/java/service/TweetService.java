package com.example.tweetapp.service;

import com.example.tweetapp.dto.*;
import com.example.tweetapp.exception.ApiException;
import com.example.tweetapp.model.Label;
import com.example.tweetapp.model.Tweet;
import com.example.tweetapp.repository.AuthorRepository;
import com.example.tweetapp.repository.LabelRepository;
import com.example.tweetapp.repository.TweetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @Transactional
    public TweetResponseTo create(TweetRequestTo req) {
        var author = authorRepo.findById(req.getAuthorId())
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, 40302, "Author not found"));

        if (repo.findByTitle(req.getTitle()).isPresent()) {
            throw new ApiException(HttpStatus.FORBIDDEN, 40305, "Tweet title already exists");
        }

        Set<Label> labels = new HashSet<>();
        if (req.getLabelIds() != null) {
            for (String labelStr : req.getLabelIds()) {
                Label label;
                try {
                    // Пытаемся понять, это ID или название
                    Long labelId = Long.parseLong(labelStr);
                    label = labelRepo.findById(labelId)
                            .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, 40303, "Label not found"));
                } catch (NumberFormatException e) {
                    // Это название. Ищем или создаем новую метку
                    label = labelRepo.findByName(labelStr).orElseGet(() -> {
                        Label newLabel = new Label();
                        newLabel.setName(labelStr);
                        return labelRepo.save(newLabel); // Сохраняем в БД!
                    });
                }
                labels.add(label);
            }
        }

        Tweet t = new Tweet();
        t.setAuthor(author);
        t.setTitle(req.getTitle());
        t.setContent(req.getContent());
        t.setLabels(labels);
        t.setCreated(Instant.now());
        t.setModified(Instant.now());

        return toDto(repo.save(t));
    }

    public List<TweetResponseTo> getAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public TweetResponseTo getById(Long id) {
        return repo.findById(id).map(this::toDto)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 40403, "Tweet not found"));
    }

    @Transactional
    public TweetResponseTo update(Long id, TweetRequestTo req) {
        Tweet t = repo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 40403, "Tweet not found"));

        var author = authorRepo.findById(req.getAuthorId())
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, 40302, "Author not found"));

        if (!t.getTitle().equals(req.getTitle()) && repo.findByTitle(req.getTitle()).isPresent()) {
            throw new ApiException(HttpStatus.FORBIDDEN, 40305, "Tweet title already exists");
        }

        Set<Label> labels = new HashSet<>();
        if (req.getLabelIds() != null) {
            for (String labelStr : req.getLabelIds()) {
                Label label;
                try {
                    Long labelId = Long.parseLong(labelStr);
                    label = labelRepo.findById(labelId)
                            .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, 40303, "Label not found"));
                } catch (NumberFormatException e) {
                    label = labelRepo.findByName(labelStr).orElseGet(() -> {
                        Label newLabel = new Label();
                        newLabel.setName(labelStr);
                        return labelRepo.save(newLabel);
                    });
                }
                labels.add(label);
            }
        }

        t.setAuthor(author);
        t.setTitle(req.getTitle());
        t.setContent(req.getContent());
        t.setLabels(labels);
        t.setModified(Instant.now());
        return toDto(repo.save(t));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ApiException(HttpStatus.NOT_FOUND, 40403, "Tweet not found");
        repo.deleteById(id);
    }

    private TweetResponseTo toDto(Tweet t) {
        TweetResponseTo dto = new TweetResponseTo();
        dto.setId(t.getId());
        dto.setAuthorId(t.getAuthor().getId());
        dto.setTitle(t.getTitle());
        dto.setContent(t.getContent());
        dto.setCreated(t.getCreated());
        dto.setModified(t.getModified());
        dto.setLabelIds(t.getLabels().stream().map(Label::getId).collect(Collectors.toSet()));
        return dto;
    }
}