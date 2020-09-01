package com.github.ayltai.hknews.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SourceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceService.class);

    private final SourceRepository sourceRepository;

    public SourceService(@NonNull final SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    @NonNull
    public List<Source> getSources() {
        if (this.sourceRepository.count() == 0L) {
            try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sources.json");
                 InputStreamReader reader = inputStream == null ? null : new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                if (reader == null) {
                    SourceService.LOGGER.warn("Failed to open sources.json");
                } else {
                    return this.sourceRepository.saveAll(new Gson().fromJson(reader, new TypeToken<List<Source>>() {}.getType()));
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

        return this.sourceRepository.findAllByName(name).orElseGet(Collections::emptyList);
    }

    @NonNull
    public List<String> getSourceNames() {
        if (this.sourceRepository.count() == 0L) return this.getSources()
            .stream()
            .map(Source::getName)
            .distinct()
            .collect(Collectors.toList());

        return this.sourceRepository
            .findDistinctNames()
            .orElseGet(Collections::emptyList);
    }

    @NonNull
    public List<Pair<String, String>> getSourceNamesAndCategoryNames() {
        final Optional<List<Object[]>> pairs = this.sourceRepository.findDistinctNamesAndCategoryNames();
        if (pairs.isEmpty()) return Collections.emptyList();

        return pairs.get()
            .stream()
            .map(pair -> Pair.of(pair[0].toString(), pair[1].toString()))
            .collect(Collectors.toList());
    }
}
