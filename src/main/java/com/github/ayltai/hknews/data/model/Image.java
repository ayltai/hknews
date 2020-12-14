package com.github.ayltai.hknews.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(
    callSuper              = true,
    onlyExplicitlyIncluded = true
)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public final class Image extends Media {
    @Getter
    @Setter
    @Column(length = Integer.MAX_VALUE)
    private String description;

    public Image(@NonNull final Item item, @NonNull final String url, @Nullable final String description) {
        super(null, item, url);

        this.description = description;
    }
}
