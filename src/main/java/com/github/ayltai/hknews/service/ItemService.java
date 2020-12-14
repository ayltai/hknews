package com.github.ayltai.hknews.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.repository.ItemRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ItemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemService.class);

    private final ItemRepository itemRepository;

    @NonNull
    public Optional<Item> getItem(@NonNull final Integer id) {
        return this.itemRepository.findById(id);
    }

    @NonNull
    public Optional<Item> getItem(@NonNull final String url) {
        return this.itemRepository.findByUrl(url);
    }

    @NonNull
    public Page<Item> getItems(@NonNull final Collection<String> sourceNames, @NonNull final Collection<String> categoryNames, @NonNull final int days, final int pageNumber, final int pageSize) {
        return itemRepository.findBySourceNameInAndCategoryNameInAndPublishDateAfterOrderByPublishDateDesc(sourceNames, categoryNames, Date.from(LocalDate.now().atStartOfDay().minusDays(1).atZone(ZoneId.systemDefault()).toInstant()), PageRequest.of(pageNumber, pageSize));
    }

    @Transactional
    public void removeItems(final int days) {
        this.itemRepository.deleteByPublishDateBefore(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).minusDays(days).toInstant()));
    }

    @NonNull
    public List<Item> putItems(@NonNull final Collection<Item> items) {
        return items.stream()
            .map(this::putItem)
            .collect(Collectors.toList());
    }

    @Transactional
    @NonNull
    protected Item putItem(@NonNull final Item item) {
        try {
            return this.itemRepository.findByUrl(item.getUrl()).isEmpty() ? this.itemRepository.save(item) : item;
        } catch (final DataAccessException e) {
            ItemService.LOGGER.warn(String.format("Unique constraint violation (URL = %s)", item.getUrl()), e);
        }

        return item;
    }
}
