package com.github.ayltai.hknews.controller;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.lang.NonNull;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.github.ayltai.hknews.UnitTests;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.service.ItemService;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

@WebMvcTest(
    controllers              = ItemController.class,
    excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class
)
public final class ItemControllerTests extends UnitTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Test
    public void when_getItem_then_returnItem() throws Exception {
        Mockito.when(this.itemService
            .getItem(ArgumentMatchers.any(String.class)))
            .thenReturn(Optional.of(ItemControllerTests.getItem()));

        this.mockMvc
            .perform(MockMvcRequestBuilders.get("/item/" + 1))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    public void when_getItems_then_returnItems() throws Exception {
        Mockito.when(this.itemService
            .getItems(ArgumentMatchers.anyList(), ArgumentMatchers.anyList(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt()))
            .thenReturn(ItemControllerTests.getItems());

        this.mockMvc
            .perform(MockMvcRequestBuilders.get("/items/蘋果日報,東方日報/港聞,國際/1"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("application/json"));
    }

    @NonNull
    private static Item getItem() {
        final Item item = new Item();
        item.setId("1");

        return item;
    }

    @NonNull
    private static Page<Item> getItems() {
        return new PageImpl<>(Arrays.asList(ItemControllerTests.getItem(), ItemControllerTests.getItem()));
    }
}
