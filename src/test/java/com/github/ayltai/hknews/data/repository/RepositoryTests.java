package com.github.ayltai.hknews.data.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

import com.github.ayltai.hknews.UnitTests;
import com.github.ayltai.hknews.data.AmazonDynamoDBFactory;
import com.github.ayltai.hknews.data.model.Model;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public abstract class RepositoryTests<R extends Repository<T>, T extends Model> extends UnitTests {
    protected final AmazonDynamoDB client;

    protected RepositoryTests() {
        this.client = AmazonDynamoDBFactory.create("http://localhost:8000");
    }

    @NotNull
    protected abstract R createRepository() throws InterruptedException;

    @AfterEach
    @Override
    public void tearDown() {
        try {
            this.createRepository().table.delete();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public abstract void testAdd();

    @Test
    public abstract void testCount();

    @Test
    public abstract void testFindAll();
}
