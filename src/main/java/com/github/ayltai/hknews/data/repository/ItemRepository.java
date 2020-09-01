package com.github.ayltai.hknews.data.repository;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.github.ayltai.hknews.data.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    @NonNull
    Page<Item> findBySourceNameInAndCategoryNameInAndPublishDateAfterOrderByPublishDateDesc(@NonNull Collection<String> sources, @NonNull Collection<String> categories, @NonNull Date publishDate, Pageable pageable);

    @NonNull
    Optional<Item> findByUrl(@NonNull String url);

    long deleteByPublishDateBefore(@NonNull Date publishDate);
}
