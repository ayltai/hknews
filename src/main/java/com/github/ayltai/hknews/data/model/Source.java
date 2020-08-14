package com.github.ayltai.hknews.data.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Document
public final class Source {
    @EqualsAndHashCode.Include
    @Getter
    @Setter
    @Id
    private String name;

    @NonNull
    @Getter
    private List<Category> categories = new ArrayList<>();
}
