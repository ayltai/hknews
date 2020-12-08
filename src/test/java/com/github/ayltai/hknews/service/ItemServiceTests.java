package com.github.ayltai.hknews.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.github.ayltai.hknews.UnitTests;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.repository.ItemRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

@SpringBootTest
public final class ItemServiceTests extends UnitTests {
    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    @Override
    public void setUp() {
        this.itemRepository.deleteAll();
    }

    @Test
    public void given_noItem_when_getItem_returnNull() {
        Assertions.assertFalse(new ItemService(this.itemRepository).getItem("1").isPresent());
    }

    @Test
    public void given_noItem_when_getItems_returnNoItem() {
        Mockito.doReturn(new PageImpl(Collections.emptyList())).when(this.itemRepository).findAllBySourceNameInAndCategoryNameInAndPublishDateAfterOrderByPublishDateDesc(ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection(), ArgumentMatchers.any(Date.class), ArgumentMatchers.any(Pageable.class));

        final Page<Item> items = new ItemService(this.itemRepository).getItems(Arrays.asList("蘋果日報", "東方日報"), Arrays.asList("港聞", "國際"), 1, 0, Integer.MAX_VALUE);

        Assertions.assertNotNull(items);
        Assertions.assertEquals(0, items.getTotalElements());
    }

    @Test
    public void given_dummyItem_when_getItem_returnDummyItem() {
        final List<Item> dbItems = new ArrayList<>();

        Mockito.doAnswer(invocation -> {
            final Item item = invocation.getArgument(0);
            dbItems.add(item);

            return item;
        }).when(this.itemRepository).save(ArgumentMatchers.any(Item.class));

        Mockito.doAnswer(invocation -> Optional.of(dbItems.stream()
            .filter(item -> item.getId().equals(invocation.getArgument(0)))
            .collect(Collectors.toList()).get(0)))
            .when(this.itemRepository)
            .findById(ArgumentMatchers.anyString());

        final Item dummyItem = new Item();
        dummyItem.setId("1");
        dummyItem.setUrl("dummy");
        dummyItem.setCategoryName("港聞");
        dummyItem.setSourceName("蘋果日報");
        dummyItem.setPublishDate(new Date());

        // Given
        this.itemRepository.save(dummyItem);

        // When
        final Optional<Item> item = new ItemService(this.itemRepository).getItem(dummyItem.getId());

        // Then
        Assertions.assertTrue(item.isPresent());
        Assertions.assertEquals(dummyItem.getId(), item.get().getId());
    }

    @Test
    public void given_dummyItem_when_getItems_returnDummyItem() {
        final List<Item> dbItems = new ArrayList<>();

        Mockito.doAnswer(invocation -> {
            final Item item = invocation.getArgument(0);
            dbItems.add(item);

            return item;
        }).when(this.itemRepository).save(ArgumentMatchers.any(Item.class));

        Mockito.doAnswer(invocation -> new PageImpl(dbItems.stream()
            .filter(item -> ((Collection<String>)invocation.getArgument(0)).contains(item.getSourceName()))
            .filter(item -> ((Collection<String>)invocation.getArgument(1)).contains(item.getCategoryName()))
            .collect(Collectors.toList())))
            .when(this.itemRepository)
            .findAllBySourceNameInAndCategoryNameInAndPublishDateAfterOrderByPublishDateDesc(ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection(), ArgumentMatchers.any(Date.class), ArgumentMatchers.any(Pageable.class));

        final Item dummyItem = new Item();
        dummyItem.setUrl("dummy");
        dummyItem.setCategoryName("港聞");
        dummyItem.setSourceName("蘋果日報");
        dummyItem.setPublishDate(new Date());

        // Given
        this.itemRepository.save(dummyItem);

        // When
        final Page<Item> items = new ItemService(this.itemRepository).getItems(Arrays.asList("蘋果日報", "東方日報"), Arrays.asList("港聞", "國際"), 1, 0, Integer.MAX_VALUE);

        // Then
        Assertions.assertNotNull(items);
        Assertions.assertEquals(1, items.getTotalElements());
        Assertions.assertEquals(dummyItem.getId(), items.get().findFirst().get().getId());
    }
}
