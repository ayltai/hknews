package com.github.ayltai.hknews.service;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.data.model.Model;
import com.github.ayltai.hknews.data.repository.Repository;

import org.jetbrains.annotations.NotNull;

public abstract class Service<R extends Repository<T>, T extends Model> {
    protected final R            repository;
    protected final LambdaLogger logger;

    protected Service(@NotNull final R repository, @NotNull final LambdaLogger logger) {
        this.repository = repository;
        this.logger     = logger;
    }
}
