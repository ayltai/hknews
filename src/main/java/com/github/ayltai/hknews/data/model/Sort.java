package com.github.ayltai.hknews.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public final class Sort {
    @Getter
    @Setter
    private boolean sorted;

    @Getter
    @Setter
    private boolean unsorted;

    @Getter
    @Setter
    private boolean empty;
}
