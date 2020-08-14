package com.github.ayltai.hknews.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    public Page<Source> getSources(final int page, final int size) {
        if (this.sourceRepository.count() == 0L) {
            try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sources.json");
                 InputStreamReader reader = inputStream == null ? null : new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                if (reader == null) {
                    SourceService.LOGGER.warn("Failed to open sources.json");
                } else {
                    return new PageImpl<>(this.sourceRepository.saveAll(new Gson().fromJson(reader, new TypeToken<List<Source>>() {}.getType())));
                }
            } catch (final IOException e) {
                SourceService.LOGGER.error(e.getMessage(), e);
            }
        }

        return this.sourceRepository.findAll(PageRequest.of(page, size));
    }
}
