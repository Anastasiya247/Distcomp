package com.example.tweetapp.web;

import com.example.tweetapp.dto.LabelRequestTo;
import com.example.tweetapp.dto.LabelResponseTo;
import com.example.tweetapp.service.LabelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/labels")
public class LabelController {

    private final LabelService service;

    public LabelController(LabelService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseTo create(@Valid @RequestBody LabelRequestTo req) {
        return service.create(req);
    }

    @GetMapping
    public List<LabelResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public LabelResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public LabelResponseTo update(@PathVariable Long id, @Valid @RequestBody LabelRequestTo req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}