package com.github.ayltai.hknews.data.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class Media {
    @Getter
    @Setter
    @GeneratedValue
    @Id
    private Integer id;

    @Getter
    @Setter
    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Item item;

    @EqualsAndHashCode.Include
    @Getter
    @Setter
    @Column(
        nullable = false,
        length   = Integer.MAX_VALUE
    )
    private String url;
}
