package com.github.ayltai.hknews.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.springframework.lang.NonNull;

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
public final class Video extends Media {
    @Getter
    @Setter
    @Column(
        nullable = false,
        length   = Integer.MAX_VALUE
    )
    private String cover;

    public Video(@NonNull final Item item, @NonNull final String url, @NonNull final String cover) {
        super(null, item, url);

        this.cover = cover;
    }
}
