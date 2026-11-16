package com.example.tweetapp.service;

import com.example.tweetapp.dto.*;
import com.example.tweetapp.exception.ApiException;
import com.example.tweetapp.model.Label;
import com.example.tweetapp.repository.LabelRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LabelService {
    private final LabelRepository repo;

    public LabelService(LabelRepository repo) { this.repo = repo; }

    @Transactional
    public LabelResponseTo create(LabelRequestTo req) {
        if (repo.findByName(req.getName()).isPresent()) {
            throw new ApiException(HttpStatus.FORBIDDEN, 40302, "Label name already exists");
        }
        Label l = new Label();
        l.setName(req.getName());
        return toDto(repo.save(l));
    }

    public List<LabelResponseTo> getAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public LabelResponseTo getById(Long id) {
        return repo.findById(id).map(this::toDto).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 40402, "Label not found"));
    }

    @Transactional
    public LabelResponseTo update(Long id, LabelRequestTo req) {
        Label l = repo.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 40402, "Label not found"));
        l.setName(req.getName());
        return toDto(repo.save(l));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ApiException(HttpStatus.NOT_FOUND, 40402, "Label not found");
        repo.deleteById(id);
    }

    private LabelResponseTo toDto(Label l) {
        LabelResponseTo dto = new LabelResponseTo();
        dto.setId(l.getId());
        dto.setName(l.getName());
        return dto;
    }
}