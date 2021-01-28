package com.github.ayltai.hknews.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SourceService extends Service<SourceRepository, Source> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceService.class);

    public SourceService(@NotNull final SourceRepository repository) {
        super(repository);
    }

    @NotNull
    public Collection<Source> getSources() {
        if (this.repository.count() == 0L) {
            try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sources.json");
                 InputStreamReader reader = inputStream == null ? null : new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                if (reader == null) {
                    SourceService.LOGGER.warn("Failed to open sources.json");
                } else {
                    this.repository.add(new Gson().fromJson(reader, new TypeToken<List<Source>>() {}.getType()));
                }
            } catch (final IOException e) {
                SourceService.LOGGER.error(e.getMessage(), e);
            }
        }

        return this.repository.findAll();
    }

    @NotNull
    public Collection<Source> getSources(@NotNull final String name) {
        if (this.repository.count() == 0L) return this.getSources()
            .stream()
            .filter(source -> source.getSourceName().equals(name))
            .collect(Collectors.toList());

        return this.repository.findBySourceNameAndCategoryName(name, null);
    }

    @NotNull
    public Collection<Source> getSources(@NotNull final String name, @NotNull final String categoryName) {
        if (this.repository.count() == 0L) return this.getSources()
            .stream()
            .filter(source -> source.getSourceName().equals(name) && source.getCategoryName().equals(categoryName))
            .collect(Collectors.toList());

        return this.repository.findBySourceNameAndCategoryName(name, categoryName);
    }
}
