package com.github.ayltai.hknews.data.repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.KeyPair;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.Configuration;
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
    public Collection<Item> findByUids(@NotNull final Collection<String> uids) {
        return this.mapper
            .batchLoad(Collections.singletonMap(Item.class, uids.stream().map(uid -> new KeyPair().withHashKey(uid)).collect(Collectors.toList())))
            .get("Item")
            .stream()
            .map(object -> (Item)object)
            .collect(Collectors.toList());
    }

    @NotNull
    public Collection<Item> findBySourceNameInAndCategoryNameInAndPublishDateAfterOrderByPublishDateDesc(@NotNull final Collection<String> sourceNames, @NotNull final Collection<String> categoryNames, final int days) {
        return this.mapper
            .scan(Item.class, new DynamoDBScanExpression()
                .withFilterExpression("PublishDate >= :PublishDate")
                .withExpressionAttributeValues(Collections.singletonMap(":PublishDate", new AttributeValue().withS(LocalDate.ofInstant(Instant.now(), ZoneId.of(Configuration.DEFAULT.getTimeZone())).atStartOfDay().minusDays(days).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))))
            .stream()
            .filter(item -> sourceNames.contains(item.getSourceName()))
            .filter(item -> categoryNames.contains(item.getCategoryName()))
            .sorted((left, right) -> right.getPublishDate().compareTo(left.getPublishDate()))
            .collect(Collectors.toList());
    }

    @NotNull
    public Collection<Item> findByPublishDateBefore(final int days) {
        return this.mapper
            .scan(Item.class, new DynamoDBScanExpression()
                .withFilterExpression("PublishDate < :PublishDate")
                .withExpressionAttributeValues(Collections.singletonMap(":PublishDate", new AttributeValue().withS(LocalDate.ofInstant(Instant.now(), ZoneId.of(Configuration.DEFAULT.getTimeZone())).atStartOfDay().minusDays(days).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))));
    }
}
