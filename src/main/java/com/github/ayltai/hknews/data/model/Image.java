package com.github.ayltai.hknews.data.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public final class Image {
    @EqualsAndHashCode.Include
    @Getter
    @Setter
    private String imageUrl;

    @Getter
    @Setter
    private String description;
}
