package com.github.ayltai.hknews.parser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.ayltai.hknews.UnitTests;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.service.SourceService;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

public abstract class ParserTests extends UnitTests {
    protected SourceService sourceService;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();

        final SourceRepository sourceRepository = Mockito.mock(SourceRepository.class);

        Mockito.doReturn(0L).when(sourceRepository).count();
        Mockito.doAnswer(invocation -> invocation.getArgument(0)).when(sourceRepository).saveAll(ArgumentMatchers.anyCollection());

        this.sourceService = new SourceService(sourceRepository);
        final List<Source> sources = this.sourceService.getSources();

        Mockito.doAnswer(invocation -> Optional.of(sources.stream()
            .filter(source -> source.getName().equals(invocation.getArgument(0)))
            .collect(Collectors.toList())))
            .when(sourceRepository)
            .findAllByName(ArgumentMatchers.anyString());
    }
}
