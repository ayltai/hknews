package com.github.ayltai.hknews.service;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import com.github.ayltai.hknews.UnitTests;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.repository.ItemRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SpringBootTest
public final class ItemServiceTests extends UnitTests {
    private static final String ITEM_ID = UUID.randomUUID().toString();

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    @Override
    public void setUp() {
        this.itemRepository.deleteAll();
    }

    @Test
    public void given_noItem_when_getItem_returnNull() {
        Assertions.assertFalse(new ItemService(this.itemRepository).getItem(ItemServiceTests.ITEM_ID).isPresent());
    }

    @Test
    public void given_noItem_when_getItems_returnNoItem() {
        final Page<Item> items = new ItemService(this.itemRepository).getItems(Arrays.asList("蘋果日報", "東方日報"), Arrays.asList("港聞", "國際"), 1, 0, Integer.MAX_VALUE);

        Assertions.assertNotNull(items);
        Assertions.assertEquals(0, items.getTotalElements());
    }

    @Test
    public void given_dummyItem_when_getItem_returnDummyItem() {
        final Item dummyItem = new Item();
        dummyItem.setId(ItemServiceTests.ITEM_ID);

        // Given
        this.itemRepository.save(dummyItem);

        // When
        final Optional<Item> item = new ItemService(this.itemRepository).getItem(ItemServiceTests.ITEM_ID);

        // Then
        Assertions.assertTrue(item.isPresent());
        Assertions.assertEquals(dummyItem.getId(), item.get().getId());
    }

    @Test
    public void given_dummyItem_when_getItems_returnDummyItem() {
        final Item dummyItem = new Item();
        dummyItem.setId(ItemServiceTests.ITEM_ID);
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
