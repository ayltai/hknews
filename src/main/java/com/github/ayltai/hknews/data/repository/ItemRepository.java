package com.github.ayltai.hknews.data.repository;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.data.model.Item;

import org.jetbrains.annotations.NotNull;

public final class ItemRepository extends Repository<Item> {
    public ItemRepository(@NotNull final AmazonDynamoDB client, @NotNull final LambdaLogger logger) throws InterruptedException {
        super(client, logger);
    }

    @Override
    public boolean add(@NotNull final Iterable<Item> models) {
        return super.add(StreamSupport.stream(models.spliterator(), false)
            .map(model -> {
                model.setUid(model.toUid());

                return model;
            })
            .collect(Collectors.toList()));
    }

    @NotNull
    public Optional<Item> findByUid(@NotNull final String uid) {
        return this.mapper
            .query(Item.class, new DynamoDBQueryExpression<Item>()
                .withKeyConditionExpression("Uid = :Uid")
                .withExpressionAttributeValues(Collections.singletonMap(":Uid", new AttributeValue().withS(uid))))
            .stream()
            .findFirst();
    }

    @NotNull
    public Collection<Item> findBySourceNameInAndCategoryNameInAndPublishDateAfterOrderByPublishDateDesc(@NotNull final Collection<String> sourceNames, @NotNull final Collection<String> categoryNames, final int days) {
        final DynamoDBScanExpression expression = new DynamoDBScanExpression();
        expression.addFilterCondition(Item.COLUMN_PUBLISH_DATE, new Condition()
            .withComparisonOperator(ComparisonOperator.GE)
            .withAttributeValueList(new AttributeValue()
                .withS(OffsetDateTime.now()
                    .minusDays(days)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));

        return this.mapper
            .scan(Item.class, expression)
            .stream()
            .filter(item -> sourceNames.contains(item.getSourceName()))
            .filter(item -> categoryNames.contains(item.getCategoryName()))
            .sorted((left, right) -> right.getPublishDate().compareTo(left.getPublishDate()))
            .collect(Collectors.toList());
    }

    @NotNull
    public Collection<Item> findByPublishDateBefore(final int days) {
        final DynamoDBScanExpression expression = new DynamoDBScanExpression();
        expression.addFilterCondition(Item.COLUMN_PUBLISH_DATE, new Condition()
            .withComparisonOperator(ComparisonOperator.LT)
            .withAttributeValueList(new AttributeValue()
                .withS(OffsetDateTime.now()
                    .minusDays(days)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));

        return this.mapper.scan(Item.class, expression);
    }
}
