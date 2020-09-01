package com.github.ayltai.hknews.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.lang.Nullable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.github.ayltai.hknews.UnitTests;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.service.SourceService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@WebMvcTest(
    controllers              = SourceController.class,
    excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
public final class SourceControllerTests extends UnitTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SourceService sourceService;

    @Test
    public void when_getSources_then_returnAllSourceNames() throws Exception {
        Mockito.when(this.sourceService.getSourceNames()).thenReturn(Objects.requireNonNull(this.getSources()).stream().map(Source::getName).collect(Collectors.toList()));

        this.mockMvc
            .perform(MockMvcRequestBuilders.get("/sources"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("application/json"));
    }

    @Nullable
    private List<Source> getSources() throws IOException {
        final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sources.json");
        if (inputStream == null) return null;

        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            List<Source> sources = new Gson().fromJson(reader, new TypeToken<List<Source>>() {}.getType());

            return new ArrayList<>(sources);
        }
    }
}
