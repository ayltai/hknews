package com.github.ayltai.hknews.data.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBDocument
public final class Image {
    @EqualsAndHashCode.Include
    @Getter(onMethod_ = {
        @DynamoDBAttribute(attributeName = "Url"),
    })
    @Setter
    private String url;

    @Getter(onMethod_ = {
        @DynamoDBAttribute(attributeName = "Description"),
    })
    @Setter
    private String description;
}
