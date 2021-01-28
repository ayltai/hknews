package com.github.ayltai.hknews.data.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.ayltai.hknews.data.model.Source;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SourceRepository extends Repository<Source> {
    public SourceRepository(@NotNull final AmazonDynamoDB client) throws InterruptedException {
        super(client);
    }

    @NotNull
    public Collection<Source> findBySourceNameAndCategoryName(@NotNull final String sourceName, @Nullable final String categoryName) {
        final Map<String, AttributeValue> values = new HashMap<>();
        values.put(":SourceName", new AttributeValue().withS(sourceName));
        if (categoryName != null) values.put(":CategoryName", new AttributeValue().withS(categoryName));

        return this.mapper
            .scan(Source.class, new DynamoDBScanExpression()
            .withFilterExpression(categoryName == null ? "SourceName = :SourceName" : "SourceName = :SourceName AND CategoryName = :CategoryName")
            .withExpressionAttributeValues(values));
    }

    @NotNull
    public Collection<String> findDistinctNames() {
        final DynamoDBScanExpression expression = new DynamoDBScanExpression()
            .withProjectionExpression("SourceName");

        return this.mapper
            .scan(Source.class, expression)
            .stream()
            .map(Source::getSourceName)
            .distinct()
            .collect(Collectors.toList());
    }
}
