package com.github.ayltai.hknews.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SourceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceService.class);

    private final SourceRepository sourceRepository;

    @NonNull
    public List<Source> getSources() {
        if (this.sourceRepository.count() == 0L) {
            try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sources.json");
                 InputStreamReader reader = inputStream == null ? null : new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                if (reader == null) {
                    SourceService.LOGGER.warn("Failed to open sources.json");
                } else {
                    return this.putSources(new Gson().fromJson(reader, new TypeToken<List<Source>>() {}.getType()));
                }
            } catch (final IOException e) {
                SourceService.LOGGER.error(e.getMessage(), e);
            }
        }

        return this.sourceRepository.findAll();
    }

    @NonNull
    public List<Source> getSources(@NonNull final String name) {
        if (this.sourceRepository.count() == 0L) return this.getSources()
            .stream()
            .filter(source -> source.getName().equals(name))
            .collect(Collectors.toList());

        return this.sourceRepository.findByName(name).orElseGet(Collections::emptyList);
    }

    @NonNull
    public List<Source> getSources(@NonNull final String name, @NonNull final String categoryName) {
        if (this.sourceRepository.count() == 0L) return this.getSources()
            .stream()
            .filter(source -> source.getName().equals(name) && source.getCategoryName().equals(categoryName))
            .collect(Collectors.toList());

        return this.sourceRepository.findByNameAndCategoryName(name, categoryName).orElseGet(Collections::emptyList);
    }

    @NonNull
    public List<String> getSourceNames() {
        if (this.sourceRepository.count() == 0L) return this.getSources()
            .stream()
            .map(Source::getName)
            .distinct()
            .collect(Collectors.toList());

        return this.sourceRepository
            .findAll()
            .stream()
            .map(Source::getName)
            .distinct()
            .collect(Collectors.toList());
    }

    @NonNull
    protected List<Source> putSources(@NonNull final Iterable<Source> sources) {
        return this.sourceRepository.saveAll(sources);
    }
}
