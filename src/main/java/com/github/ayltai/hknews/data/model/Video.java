package com.github.ayltai.hknews.data.model;

import org.springframework.lang.NonNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(
    callSuper              = true,
    onlyExplicitlyIncluded = true
)
@NoArgsConstructor
public final class Video extends Media {
    @Getter
    @Setter
    private String cover;

    public Video(@NonNull final String url, @NonNull final String cover) {
        super(url);

        this.cover = cover;
    }
}
