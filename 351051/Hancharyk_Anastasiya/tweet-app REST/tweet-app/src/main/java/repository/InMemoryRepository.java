package com.example.tweetapp.repository;

import com.example.tweetapp.model.BaseEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class InMemoryRepository<T extends BaseEntity> implements Repository<T> {
    protected final Map<Long, T> storage = new ConcurrentHashMap<>();
    protected final AtomicLong idSequence = new AtomicLong(0);

    @Override
    public T save(T entity) {
        if (entity.getId() == null) {
            entity.setId(idSequence.incrementAndGet());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}
