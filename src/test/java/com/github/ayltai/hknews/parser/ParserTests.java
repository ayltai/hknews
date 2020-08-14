package com.github.ayltai.hknews.parser;

import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;

import com.github.ayltai.hknews.UnitTests;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.service.SourceService;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@DataMongoTest
public abstract class ParserTests extends UnitTests {
    @Mock
    protected SourceRepository sourceRepository;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();

        MockitoAnnotations.initMocks(this);

        Mockito.doReturn(0L).when(this.sourceRepository).count();
        Mockito.doAnswer(invocation -> invocation.getArgument(0)).when(this.sourceRepository).saveAll(ArgumentMatchers.anyCollection());

        final Page<Source> sources = new SourceService(this.sourceRepository).getSources(0, Integer.MAX_VALUE);

        Mockito.doAnswer(invocation -> sources.get()
            .filter(source -> source.getName().equals(invocation.getArgument(0)))
            .findFirst()
            .get())
            .when(this.sourceRepository)
            .findByName(ArgumentMatchers.anyString());
    }
}
