package com.github.ayltai.hknews.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.repository.ItemRepository;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(@NonNull final ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @NonNull
    public Optional<Item> getItem(@NonNull final Integer id) {
        return this.itemRepository.findById(id);
    }

    @NonNull
    public Page<Item> getItems(@NonNull final Collection<String> sourceNames, @NonNull final Collection<String> categoryNames, final int days, final int page, final int size) {
        return this.itemRepository.findBySourceNameInAndCategoryNameInAndPublishDateAfterOrderByPublishDateDesc(sourceNames, categoryNames, Date.from(LocalDate.now().atStartOfDay().minusDays(1).atZone(ZoneId.systemDefault()).toInstant()), PageRequest.of(page, size));
    }

    public void saveItems(@NonNull final Collection<Item> items) {
        items.stream()
            .filter(item -> this.itemRepository.findByUrl(item.getUrl()).isEmpty())
            .forEach(this.itemRepository::save);
    }
}
