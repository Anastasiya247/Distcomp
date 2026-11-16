package com.example.tweetapp.repository;

import com.example.tweetapp.model.Label;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class LabelRepository extends InMemoryRepository<Label> {
    public Optional<Label> findByName(String name) {
        return storage.values().stream().filter(l -> l.getName().equals(name)).findFirst();
    }
}
