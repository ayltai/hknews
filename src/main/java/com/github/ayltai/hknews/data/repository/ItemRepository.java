package com.github.ayltai.hknews.data.repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.ayltai.hknews.data.model.Item;

import org.jetbrains.annotations.NotNull;

public final class ItemRepository extends Repository<Item> {
    public ItemRepository(@NotNull final AmazonDynamoDB client) throws InterruptedException {
        super(client);
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
        final OffsetDateTime              date   = LocalDate.now().atStartOfDay().atOffset(ZoneOffset.UTC).minusDays(days);
        final Map<String, AttributeValue> values = new HashMap<>();

        values.put(":SourceNames", new AttributeValue().withL(sourceNames.stream().map(sourceName -> new AttributeValue().withS(sourceName)).collect(Collectors.toList())));
        values.put(":CategoryNames", new AttributeValue().withL(categoryNames.stream().map(categoryName -> new AttributeValue().withS(categoryName)).collect(Collectors.toList())));
        values.put(":PublishDate", new AttributeValue().withS(date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

        return this.mapper
            .scan(Item.class, new DynamoDBScanExpression()
                .withFilterExpression("PublishDate >= :PublishDate AND contains(:SourceNames, SourceName) AND contains(:CategoryNames, CategoryName)")
                .withExpressionAttributeValues(values))
            .stream()
            .sorted((left, right) -> right.getPublishDate().compareTo(left.getPublishDate()))
            .collect(Collectors.toList());
    }
}
