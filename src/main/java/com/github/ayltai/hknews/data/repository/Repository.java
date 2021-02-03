package com.github.ayltai.hknews.data.repository;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.BillingMode;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.data.model.Model;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

public abstract class Repository<T extends Model> {
    private static final Gson GSON = new Gson();

    protected final DynamoDB       client;
    protected final Table          table;
    protected final DynamoDBMapper mapper;

    private final Class<T>     modelClass;
    private final LambdaLogger logger;

    protected Repository(@NotNull final AmazonDynamoDB client, @NotNull final LambdaLogger logger) throws InterruptedException {
        this.client     = new DynamoDB(client);
        this.modelClass = (Class<T>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.logger     = logger;

        if (StreamSupport.stream(this.client.listTables().spliterator(), false).noneMatch(table -> this.modelClass.getSimpleName().equals(table.getTableName()))) {
            this.client
                .createTable(new CreateTableRequest()
                .withTableName(this.modelClass.getSimpleName())
                .withKeySchema(this.getKeySchemaElements())
                .withAttributeDefinitions(this.getAttributeDefinitions())
                .withBillingMode(BillingMode.PAY_PER_REQUEST))
                .waitForActive();
        }

        this.table  = this.client.getTable(this.modelClass.getSimpleName());
        this.mapper = new DynamoDBMapper(client);
    }

    public long count() {
        return this.table.describe().getItemCount();
    }

    @NotNull
    public Collection<T> findAll() {
        return this.mapper.scan(this.modelClass, new DynamoDBScanExpression());
    }

    public boolean add(@NotNull final Iterable<T> models) {
        return this.change(models, true, false);
    }

    public boolean remove(@NotNull final Iterable<T> models) {
        return this.change(models, false, true);
    }

    private boolean change(@NotNull final Iterable<T> models, final boolean add, final boolean remove) {
        final List<DynamoDBMapper.FailedBatch> failedBatches = this.mapper.batchWrite(add ? models : Collections.emptyList(), remove ? models : Collections.emptyList());

        if (!failedBatches.isEmpty()) {
            this.logFailedBatches(failedBatches);

            return false;
        }

        return true;
    }

    private void logFailedBatches(@NotNull final List<DynamoDBMapper.FailedBatch> failedBatches) {
        failedBatches.forEach(failedBatch -> {
            this.logger.log(Repository.GSON.toJson(failedBatch.getException()));

            failedBatch.getUnprocessedItems()
                .values()
                .stream()
                .flatMap(List::stream)
                .forEach(request -> this.logger.log(Repository.GSON.toJson(request.getPutRequest().getItem())));
        });
    }

    @NotNull
    private Collection<KeySchemaElement> getKeySchemaElements() {
        final List<KeySchemaElement> elements = new ArrayList<>();

        for (final Method method : this.modelClass.getMethods()) {
            if (method.getAnnotation(DynamoDBHashKey.class) != null) {
                elements.add(0, new KeySchemaElement(Repository.toAttributeName(method.getName()), KeyType.HASH));
            } else if (method.getAnnotation(DynamoDBRangeKey.class) != null) {
                elements.add(new KeySchemaElement(Repository.toAttributeName(method.getName()), KeyType.RANGE));
            }
        }

        return elements;
    }

    @NotNull
    private Collection<AttributeDefinition> getAttributeDefinitions() {
        final List<AttributeDefinition> definitions = new ArrayList<>();

        for (final Method method : this.modelClass.getMethods()) {
            if (method.getAnnotation(DynamoDBHashKey.class) != null || method.getAnnotation(DynamoDBRangeKey.class) != null) definitions.add(new AttributeDefinition(Repository.toAttributeName(method.getName()), ScalarAttributeType.S));
        }

        return definitions;
    }

    private static String toAttributeName(final String getterMethodName) {
        return getterMethodName.substring("get".length());
    }
}
