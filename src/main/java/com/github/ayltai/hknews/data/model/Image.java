package com.github.ayltai.hknews.data.model;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(
    callSuper              = true,
    onlyExplicitlyIncluded = true
)
@NoArgsConstructor
public final class Image extends Media {
    @Getter
    @Setter
    private String description;

    public Image(@NonNull final String url, @Nullable final String description) {
        super(url);

        this.description = description;
    }
}
