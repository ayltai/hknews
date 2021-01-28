package com.github.ayltai.hknews.parser;

import java.io.IOException;

import com.github.ayltai.hknews.UnitTests;
import com.github.ayltai.hknews.data.AmazonDynamoDBFactory;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.service.SourceService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class ParserTests extends UnitTests {
    protected SourceService sourceService;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();

        try {
            this.sourceService = new SourceService(new SourceRepository(AmazonDynamoDBFactory.create("http://localhost:8000")));
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public abstract void testGetItems() throws Exception;

    @Test
    public abstract void testUpdateItem() throws IOException;
}
