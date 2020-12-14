package com.github.ayltai.hknews.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
    @Index(columnList = "name,categoryName,url"),
})
@Entity
public final class Source {
    @Getter
    @Setter
    @GeneratedValue
    @Id
    private Integer id;

    @Getter
    @Setter
    @Column(nullable = false)
    private String name;

    @Getter
    @Setter
    @Column(nullable = false)
    private String categoryName;

    @Getter
    @Setter
    @Column(
        nullable = false,
        length   = Integer.MAX_VALUE
    )
    private String url;
}
