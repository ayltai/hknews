package com.github.ayltai.hknews.data.model;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@DynamoDBTable(tableName = "Item")
public final class Item implements Model {
    @Getter(onMethod_ = {
        @DynamoDBHashKey(attributeName = "Uid"),
    })
    @Setter
    private String uid;

    @Getter(onMethod_ = {
        @DynamoDBAttribute(attributeName = "Title"),
    })
    @Setter
    private String title;

    @Getter(onMethod_ = {
        @DynamoDBAttribute(attributeName = "Description"),
    })
    @Setter
    private String description;

    @Getter(onMethod_ = {
        @DynamoDBAttribute(attributeName = "Url"),
    })
    @Setter
    private String url;

    @Getter(onMethod_ = {
        @SuppressFBWarnings(
            value         = "EI_EXPOSE_REP",
            justification = "Date is the only supported date/time type in Amazon DynamoDB"),
        @DynamoDBRangeKey(attributeName = "PublishDate"),
    })
    @Setter(onMethod_ = {
        @SuppressFBWarnings(
            value         = "EI_EXPOSE_REP2",
            justification = "Date is the only supported date/time type in Amazon DynamoDB"),
    })
    private Date publishDate;

    @Getter(onMethod_ = {
        @DynamoDBAttribute(attributeName = "SourceName"),
    })
    @Setter
    private String sourceName;

    @Getter(onMethod_ = {
        @DynamoDBAttribute(attributeName = "CategoryName"),
    })
    @Setter
    private String categoryName;

    @Getter(onMethod_ = {
        @DynamoDBAttribute(attributeName = "Images"),
    })
    @Setter
    @JsonManagedReference
    private List<Image> images = new ArrayList<>();

    @Getter(onMethod_ = {
        @DynamoDBAttribute(attributeName = "Videos"),
    })
    @Setter
    @JsonManagedReference
    private List<Video> videos = new ArrayList<>();

    public String toUid() {
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(this.url.getBytes(StandardCharsets.UTF_8));

            return String.format("%032x", new BigInteger(1, digest.digest()));
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(@Nullable final Object another) {
        if (this == another) return true;
        if (another == null || this.getClass() != another.getClass()) return false;

        return this.url.equals(((Item)another).url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uid);
    }
}
