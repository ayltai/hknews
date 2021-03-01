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
    public static final String COLUMN_SOURCE_NAME   = "SourceName";
    public static final String COLUMN_CATEGORY_NAME = "CategoryName";
    public static final String COLUMN_URL           = "Url";

    @Getter(onMethod_ = {
        @DynamoDBAttribute(attributeName = Source.COLUMN_SOURCE_NAME),
    })
    @Setter
    private String sourceName;

    @Getter(onMethod_ = {
        @DynamoDBRangeKey(attributeName = Source.COLUMN_CATEGORY_NAME),
    })
    @Setter
    private String categoryName;


    @Getter(onMethod_ = {
        @DynamoDBHashKey(attributeName = Source.COLUMN_URL),
    })
    @Setter
    private String url;
}
