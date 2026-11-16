package com.example.tweetapp.service;

import com.example.tweetapp.dto.LabelRequestTo;
import com.example.tweetapp.dto.LabelResponseTo;
import com.example.tweetapp.exception.ApiException;
import com.example.tweetapp.model.Label;
import com.example.tweetapp.model.Tweet;
import com.example.tweetapp.repository.LabelRepository;
import com.example.tweetapp.repository.TweetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LabelService {
    private final LabelRepository repo;
    private final TweetRepository tweetRepository; // Добавили репозиторий твитов

    public LabelService(LabelRepository repo, TweetRepository tweetRepository) {
        this.repo = repo;
        this.tweetRepository = tweetRepository;
    }

    public LabelResponseTo create(LabelRequestTo req) {
        if (repo.findByName(req.getName()).isPresent()) throw new ApiException(HttpStatus.CONFLICT, 40902, "Label name already exists");
        Label label = new Label();
        label.setName(req.getName());
        return toDto(repo.save(label));
    }

    public List<LabelResponseTo> getAll() { return repo.findAll().stream().map(this::toDto).collect(Collectors.toList()); }

    public LabelResponseTo getById(Long id) {
        return toDto(repo.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 40402, "Label not found")));
    }

    // НОВЫЙ МЕТОД: Получить метки (Labels) по ID твита
    public List<LabelResponseTo> getLabelsByTweetId(Long tweetId) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 40405, "Tweet not found"));

        return tweet.getLabelIds().stream()
                .map(labelId -> getById(labelId))
                .collect(Collectors.toList());
    }

    public LabelResponseTo update(Long id, LabelRequestTo req) {
        Label label = repo.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 40402, "Label not found"));
        repo.findByName(req.getName()).ifPresent(l -> {
            if (!l.getId().equals(id)) throw new ApiException(HttpStatus.CONFLICT, 40902, "Label name already exists");
        });
        label.setName(req.getName());
        return toDto(repo.save(label));
    }

    public void delete(Long id) {
        if (!repo.deleteById(id)) throw new ApiException(HttpStatus.NOT_FOUND, 40402, "Label not found");
    }

    private LabelResponseTo toDto(Label label) {
        LabelResponseTo dto = new LabelResponseTo();
        dto.setId(label.getId());
        dto.setName(label.getName());
        return dto;
    }
}