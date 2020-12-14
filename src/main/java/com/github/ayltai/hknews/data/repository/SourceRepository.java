package com.github.ayltai.hknews.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.github.ayltai.hknews.data.model.Source;

@Repository
public interface SourceRepository extends JpaRepository<Source, Integer> {
    @NonNull
    @Query("SELECT DISTINCT name FROM Source")
    Optional<List<String>> findDistinctNames();

    @NonNull
    Optional<List<Source>> findByName(@NonNull String name);

    @NonNull
    Optional<List<Source>> findByNameAndCategoryName(@NonNull String name, @NonNull String categoryName);
}
