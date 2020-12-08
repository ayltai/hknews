package com.github.ayltai.hknews.data.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@CompoundIndexes({
    @CompoundIndex(def = "{'sourceName': 1, 'categoryName': 1}")
})
@Document
public final class Item {
    @Getter
    @Setter
    @Id
    private String id;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String description;

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
