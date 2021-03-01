package com.github.ayltai.hknews.data.repository;

import java.util.Collection;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.data.model.Source;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SourceRepository extends Repository<Source> {
    public SourceRepository(@NotNull final AmazonDynamoDB client, @NotNull final LambdaLogger logger) throws InterruptedException {
        super(client, logger);
    }

    @NotNull
    public Collection<Source> findBySourceNameAndCategoryName(@NotNull final String sourceName, @Nullable final String categoryName) {
        final DynamoDBScanExpression expression = new DynamoDBScanExpression();
        expression.addFilterCondition(Source.COLUMN_SOURCE_NAME, new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(sourceName)));
        if (categoryName != null) expression.addFilterCondition(Source.COLUMN_CATEGORY_NAME, new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(categoryName)));

        return this.mapper.scan(Source.class, expression);
    }
}
