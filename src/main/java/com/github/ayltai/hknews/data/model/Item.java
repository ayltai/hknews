package com.github.ayltai.hknews.data.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

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
@Document
public final class Item {
    @Getter
    @Setter
    @Id
    private String id;

    @Getter
    @Setter
    @TextIndexed
    private String title;

    @Getter
    @Setter
    @TextIndexed
    private String description;

    @EqualsAndHashCode.Include
    @Getter
    @Setter
    @Indexed(unique = true)
    private String url;

    @Getter
    @Setter
    @Indexed(direction = IndexDirection.DESCENDING)
    private Date publishDate;

    @Getter
    @Setter
    @Indexed
    private String sourceName;

    @Getter
    @Setter
    @Indexed
    private String categoryName;

    @NonNull
    @Getter
    @Setter
    private List<Image> images = new ArrayList<>();

    @NonNull
    @Getter
    @Setter
    private List<Video> videos = new ArrayList<>();
}
