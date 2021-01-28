package com.github.ayltai.hknews.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public final class Pageable {
    @Getter
    @Setter
    private Sort sort;

    @Getter
    @Setter
    private int offset;

    @Getter
    @Setter
    private int pageSize;

    @Getter
    @Setter
    private int pageNumber;

    @Getter
    @Setter
    private boolean unpaged;

    @Getter
    @Setter
    private boolean paged;
}
