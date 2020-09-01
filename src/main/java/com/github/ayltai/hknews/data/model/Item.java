package com.github.ayltai.hknews.data.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {
    @Index(
        columnList = "url",
        unique     = true),
    @Index(columnList = "publishDate DESC"),
    @Index(columnList = "title"),
    @Index(columnList = "description"),
    @Index(columnList = "sourceName,categoryName"),
})
public final class Item {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Getter
    @Setter
    @Column(length = Integer.MAX_VALUE)
    private String title;

    @Getter
    @Setter
    @Column(length = Integer.MAX_VALUE)
    private String description;

    @EqualsAndHashCode.Include
    @Getter
    @Setter
    @Column(
        nullable = false,
        unique   = true,
        length   = Integer.MAX_VALUE)
    private String url;

    @Getter
    @Setter
    @Column(nullable = false)
    private Date publishDate;

    @Getter
    @Setter
    @Column(nullable = false)
    private String sourceName;

    @Getter
    @Setter
    @Column(nullable = false)
    private String categoryName;

    @Getter
    @JsonManagedReference
    @OneToMany(
        cascade       = CascadeType.ALL,
        orphanRemoval = true)
    private final List<Image> images = new ArrayList<>();

    @Getter
    @JsonManagedReference
    @OneToMany(
        cascade       = CascadeType.ALL,
        orphanRemoval = true)
    private final List<Video> videos = new ArrayList<>();
}
