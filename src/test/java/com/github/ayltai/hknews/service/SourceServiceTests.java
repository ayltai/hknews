package com.github.ayltai.hknews.service;

import java.util.List;

import org.springframework.boot.test.context.SpringBootTest;

import com.github.ayltai.hknews.UnitTests;
import com.github.ayltai.hknews.data.repository.SourceRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

@SpringBootTest
public final class SourceServiceTests extends UnitTests {
    @Mock
    private SourceRepository sourceRepository;

    @Test
    public void given_noSource_when_getSources_then_returnSourceNames() {
        Mockito.doReturn(0L).when(this.sourceRepository).count();
        Mockito.doAnswer(invocation -> invocation.getArgument(0)).when(sourceRepository).saveAll(ArgumentMatchers.anyCollection());

        final List<String> sourceNames = new SourceService(this.sourceRepository).getSourceNames();

        Assertions.assertNotNull(sourceNames);
        Assertions.assertEquals(16, sourceNames.size());
    }
}
