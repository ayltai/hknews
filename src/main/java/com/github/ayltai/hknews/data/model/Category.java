package com.github.ayltai.hknews.data.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
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
public final class Category {
    @EqualsAndHashCode.Include
    @Getter
    @Setter
    @Indexed
    private String name;

    @NonNull
    @Getter
    @Setter
    private List<String> urls = new ArrayList<>();
}
