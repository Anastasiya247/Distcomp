package com.example.tweetapp.repository;

import com.example.tweetapp.model.Author;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class AuthorRepository extends InMemoryRepository<Author> {
    public Optional<Author> findByLogin(String login) {
        return storage.values().stream().filter(a -> a.getLogin().equals(login)).findFirst();
    }
}
