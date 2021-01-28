package com.github.ayltai.hknews.data.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "Source")
public final class Source implements Model {
    @Getter(onMethod_ = {
        @DynamoDBAttribute(attributeName = "SourceName"),
    })
    @Setter
    private String sourceName;

    @Getter(onMethod_ = {
        @DynamoDBRangeKey(attributeName = "CategoryName"),
    })
    @Setter
    private String categoryName;


    @Getter(onMethod_ = {
        @DynamoDBHashKey(attributeName = "Url"),
    })
    @Setter
    private String url;
}
