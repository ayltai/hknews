package com.github.ayltai.hknews.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.github.ayltai.hknews.data.model.Source;

@Repository
public interface SourceRepository extends JpaRepository<Source, String> {
    @NonNull
    @Query("select distinct name from Source")
    Optional<List<String>> findDistinctNames();

    @NonNull
    @Query("select distinct name, categoryName from Source")
    Optional<List<Object[]>> findDistinctNamesAndCategoryNames();

    @NonNull
    Optional<List<Source>> findAllByName(@NonNull String name);
}
