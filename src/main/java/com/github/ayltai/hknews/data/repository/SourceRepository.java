package com.github.ayltai.hknews.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.github.ayltai.hknews.data.model.Source;

public interface SourceRepository extends MongoRepository<Source, String> {
    @Nullable
    Source findByName(@NonNull String name);
}
