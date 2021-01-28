package com.github.ayltai.hknews.data.model;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public final class Page<T> {
    @Getter
    @Setter
    private Pageable pageable;

    @Getter
    @Setter
    private Sort sort;

    @Getter
    @Setter
    private int size;

    @Getter
    @Setter
    private int totalPages;

    @Getter
    @Setter
    private int totalElements;

    @Getter
    @Setter
    private int numberOfElements;

    @Getter
    @Setter
    private boolean first;

    @Getter
    @Setter
    private boolean last;

    @Getter
    @Setter
    private boolean empty;

    @Getter
    @Setter
    private Collection<T> content;
}
