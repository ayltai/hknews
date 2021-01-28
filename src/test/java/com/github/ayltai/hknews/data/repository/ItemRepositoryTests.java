package com.github.ayltai.hknews.data.repository;

import java.util.Calendar;
import java.util.Collections;

import com.github.ayltai.hknews.data.model.Item;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class ItemRepositoryTests extends RepositoryTests<ItemRepository, Item> {
    @NotNull
    @Override
    protected ItemRepository createRepository() throws InterruptedException {
        return new ItemRepository(this.client);
    }

    @Test
    @Override
    public void testAdd() {
        Assertions.assertDoesNotThrow(() -> Assertions.assertTrue(this.createRepository().add(Collections.singletonList(ItemRepositoryTests.createItem()))));
    }

    @Test
    @Override
    public void testCount() {
        try {
            final ItemRepository repository = this.createRepository();
            repository.add(Collections.singletonList(ItemRepositoryTests.createItem()));

            Assertions.assertEquals(1, repository.count());
        } catch (final InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @Test
    @Override
    public void testFindAll() {
        try {
            final ItemRepository repository = this.createRepository();
            repository.add(Collections.singletonList(ItemRepositoryTests.createItem()));

            Assertions.assertEquals("title", repository.findAll().iterator().next().getTitle());
        } catch (final InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testFindByUid() {
        try {
            final ItemRepository repository = this.createRepository();
            repository.add(Collections.singletonList(ItemRepositoryTests.createItem()));

            Assertions.assertTrue(repository.findByUid("572d4e421e5e6b9bc11d815e8a027112").isPresent());
            Assertions.assertTrue(repository.findByUid("dummy").isEmpty());
        } catch (final InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testFindBySourceNameInAndCategoryNameInAndPublishDateAfterOrderByPublishDateDesc() {
        try {
            final ItemRepository repository = this.createRepository();
            repository.add(Collections.singletonList(ItemRepositoryTests.createItem()));

            Assertions.assertEquals("title", repository.findBySourceNameInAndCategoryNameInAndPublishDateAfterOrderByPublishDateDesc(Collections.singletonList("sourceName"), Collections.singletonList("categoryName"), 1).iterator().next().getTitle());
            Assertions.assertEquals(0, repository.findBySourceNameInAndCategoryNameInAndPublishDateAfterOrderByPublishDateDesc(Collections.emptyList(), Collections.singletonList("categoryName"), 1).size());
            Assertions.assertEquals(0, repository.findBySourceNameInAndCategoryNameInAndPublishDateAfterOrderByPublishDateDesc(Collections.singletonList("sourceName"), Collections.emptyList(), 1).size());
            Assertions.assertEquals(0, repository.findBySourceNameInAndCategoryNameInAndPublishDateAfterOrderByPublishDateDesc(Collections.singletonList("sourceName"), Collections.singletonList("categoryName"), 0).size());
        } catch (final InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @NotNull
    private static Item createItem() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);

        final Item item = new Item();
        item.setTitle("title");
        item.setDescription("description");
        item.setUrl("url");
        item.setPublishDate(calendar.getTime());
        item.setSourceName("sourceName");
        item.setCategoryName("categoryName");
        item.setImages(Collections.emptyList());
        item.setVideos(Collections.emptyList());

        return item;
    }
}
