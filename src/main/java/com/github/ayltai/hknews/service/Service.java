package com.github.ayltai.hknews.service;

import com.github.ayltai.hknews.data.model.Model;
import com.github.ayltai.hknews.data.repository.Repository;

import org.jetbrains.annotations.NotNull;

public abstract class Service<R extends Repository<T>, T extends Model> {
    protected final R repository;

    protected Service(@NotNull final R repository) {
        this.repository = repository;
    }
}
