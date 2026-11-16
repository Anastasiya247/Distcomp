package com.example.tweetapp.repository;

import com.example.tweetapp.model.BaseEntity;
import java.util.List;
import java.util.Optional;

public interface Repository<T extends BaseEntity> {
    T save(T entity);
    Optional<T> findById(Long id);
    List<T> findAll();
    boolean deleteById(Long id);
    boolean existsById(Long id);
}
