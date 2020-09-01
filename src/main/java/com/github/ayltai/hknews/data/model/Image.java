package com.github.ayltai.hknews.data.model;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(
    onlyExplicitlyIncluded = true,
    callSuper              = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {
    @Index(
        columnList = "item_id,url",
        unique     = true),
})
public final class Image extends Media {
    public Image(@Nullable final Integer id, @NonNull final Item item, @NonNull final String url, @Nullable final String description) {
        super(id, item, url);

        this.description = description;
    }

    @Getter
    @Setter
    private String description;
}
