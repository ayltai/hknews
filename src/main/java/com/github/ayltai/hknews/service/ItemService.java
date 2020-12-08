package com.github.ayltai.hknews.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ItemService {
    private final ItemRepository itemRepository;

    @NonNull
    public Optional<Item> getItem(@NonNull final String id) {
        return this.itemRepository.findById(id);
    }

    @NonNull
    public Page<Item> getItems(@NonNull final Collection<String> sourceNames, @NonNull final Collection<String> categoryNames, @NonNull final int days, final int pageNumber, final int pageSize) {
        return itemRepository.findAllBySourceNameInAndCategoryNameInAndPublishDateAfterOrderByPublishDateDesc(sourceNames, categoryNames, Date.from(LocalDate.now().atStartOfDay().minusDays(1).atZone(ZoneId.systemDefault()).toInstant()), PageRequest.of(pageNumber, pageSize));
    }

    @NonNull
    public List<Item> putItems(@NonNull final Collection<Item> items) {
        return this.itemRepository.saveAll(items.stream()
            .filter(item -> this.itemRepository.findByUrl(item.getUrl()).isEmpty())
            .collect(Collectors.toList()));
    }
}
