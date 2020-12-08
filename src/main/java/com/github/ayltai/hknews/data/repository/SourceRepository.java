package com.github.ayltai.hknews.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.github.ayltai.hknews.data.model.Source;

@Repository
public interface SourceRepository extends MongoRepository<Source, String> {
    @NonNull
    Optional<List<Source>> findByName(@NonNull String name);

    @NonNull
    Optional<List<Source>> findByNameAndCategoryName(@NonNull String name, @NonNull final String categoryName);
}
