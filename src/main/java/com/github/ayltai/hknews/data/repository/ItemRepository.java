package com.github.ayltai.hknews.data.repository;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.github.ayltai.hknews.data.model.Item;

@Repository
public interface ItemRepository extends MongoRepository<Item, String> {
    @NonNull
    Page<Item> findAllBySourceNameInAndCategoryNameInAndPublishDateAfterOrderByPublishDateDesc(@NonNull Collection<String> sourceNames, @NonNull Collection<String> categoryNames, @NonNull Date publishDate, Pageable pageable);

    @NonNull
    Optional<Item> findByUrl(@NonNull String url);

    long deleteAllByPublishDateBefore(@NonNull Date publishDate);
}
