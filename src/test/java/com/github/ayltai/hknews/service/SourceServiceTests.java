package com.github.ayltai.hknews.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.ayltai.hknews.UnitTests;
import com.github.ayltai.hknews.data.repository.SourceRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SpringBootTest
public final class SourceServiceTests extends UnitTests {
    @Autowired
    private SourceRepository sourceRepository;

    @Test
    public void given_noSource_when_getSources_then_returnSourceNames() {
        final List<String> sourceNames = new SourceService(this.sourceRepository).getSourceNames();

        Assertions.assertNotNull(sourceNames);
        Assertions.assertEquals(16, sourceNames.size());
    }
}
