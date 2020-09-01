package com.github.ayltai.hknews.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(
    onlyExplicitlyIncluded = true,
    callSuper              = true)
@NoArgsConstructor
@Entity
@Table(indexes = {
    @Index(
        columnList = "item_id,url",
        unique     = true),
})
public final class Video extends Media {
    public Video(@Nullable final Integer id, @NonNull final Item item, @NonNull final String url, @NonNull final String cover) {
        super(id, item, url);

        this.cover = cover;
    }

    @Getter
    @Setter
    @Column(nullable = false)
    private String cover;
}
