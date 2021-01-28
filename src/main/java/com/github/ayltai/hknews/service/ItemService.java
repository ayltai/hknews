package com.github.ayltai.hknews.service;

import java.util.Collection;
import java.util.Optional;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.repository.ItemRepository;

import org.jetbrains.annotations.NotNull;

public final class ItemService extends Service<ItemRepository, Item> {
    public ItemService(@NotNull final ItemRepository repository, @NotNull final LambdaLogger logger) {
        super(repository, logger);
    }

    @NotNull
    public Optional<Item> getItem(@NotNull final String uid) {
        return this.repository.findByUid(uid);
    }

    @NotNull
    public Collection<Item> getItems(@NotNull final Collection<String> sourceNames, @NotNull final Collection<String> categoryNames, final int days) {
        return this.repository.findBySourceNameInAndCategoryNameInAndPublishDateAfterOrderByPublishDateDesc(sourceNames, categoryNames, days);
    }

    @NotNull
    public Collection<Item> putItems(@NotNull final Collection<Item> items) {
        this.repository.add(items);

        return items;
    }

    public void removeOldItems(final int days) {
        this.repository.remove(this.repository.findByPublishDateBefore(days));
    }
}
