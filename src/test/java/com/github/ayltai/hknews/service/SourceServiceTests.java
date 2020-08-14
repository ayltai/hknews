package com.github.ayltai.hknews.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import com.github.ayltai.hknews.UnitTests;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.repository.SourceRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SpringBootTest
public final class SourceServiceTests extends UnitTests {
    @Autowired
    private SourceRepository sourceRepository;

    @Test
    public void given_noSource_when_getSources_then_returnSources() {
        final Page<Source> sources = new SourceService(this.sourceRepository).getSources(0, Integer.MAX_VALUE);

        Assertions.assertNotNull(sources);
        Assertions.assertEquals(16, sources.getTotalElements());
    }
}
