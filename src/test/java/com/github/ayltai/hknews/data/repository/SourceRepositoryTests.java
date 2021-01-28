package com.github.ayltai.hknews.data.repository;

import java.util.Collections;

import com.github.ayltai.hknews.data.model.Source;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class SourceRepositoryTests extends RepositoryTests<SourceRepository, Source> {
    @NotNull
    @Override
    protected SourceRepository createRepository() throws InterruptedException {
        return new SourceRepository(this.client);
    }

    @Test
    @Override
    public void testAdd() {
        Assertions.assertDoesNotThrow(() -> Assertions.assertTrue(this.createRepository().add(Collections.singletonList(SourceRepositoryTests.createSource()))));
    }

    @Test
    @Override
    public void testCount() {
        try {
            final SourceRepository repository = this.createRepository();
            repository.add(Collections.singletonList(SourceRepositoryTests.createSource()));

            Assertions.assertEquals(1, repository.count());
        } catch (final InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @Test
    @Override
    public void testFindAll() {
        try {
            final SourceRepository repository = this.createRepository();
            repository.add(Collections.singletonList(SourceRepositoryTests.createSource()));

            Assertions.assertEquals("sourceName", repository.findAll().iterator().next().getSourceName());
        } catch (final InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testFindByNameAndCategoryName() {
        try {
            final SourceRepository repository = this.createRepository();
            repository.add(Collections.singletonList(SourceRepositoryTests.createSource()));

            Assertions.assertEquals("sourceName", repository.findBySourceNameAndCategoryName("sourceName", "categoryName").iterator().next().getSourceName());
            Assertions.assertEquals("sourceName", repository.findBySourceNameAndCategoryName("sourceName", null).iterator().next().getSourceName());
            Assertions.assertEquals(0, repository.findBySourceNameAndCategoryName("sourceName", "dummy").size());
            Assertions.assertEquals(0, repository.findBySourceNameAndCategoryName("dummy", "dummy").size());
        } catch (final InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testFindDistinctNames() {
        try {
            final SourceRepository repository = this.createRepository();
            repository.add(Collections.singletonList(SourceRepositoryTests.createSource()));

            Assertions.assertEquals("sourceName", repository.findDistinctNames().iterator().next());
        } catch (final InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @NotNull
    private static Source createSource() {
        return new Source("sourceName", "categoryName", "url");
    }
}
