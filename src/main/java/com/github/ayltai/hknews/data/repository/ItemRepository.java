package com.github.ayltai.hknews.data.repository;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Item;

public interface ItemRepository extends MongoRepository<Item, String> {
    @NonNull
    Page<Item> findBySourceNameInAndCategoryNameInAndPublishDateAfterOrderByPublishDateDesc(@NonNull Collection<String> sourceNames, @NonNull Collection<String> categoryNames, @NonNull Date publishDate, Pageable pageable);

    @NonNull
    Optional<Item> findByUrl(@NonNull String url);

    long deleteByPublishDateBefore(@NonNull Date publishDate);
}
