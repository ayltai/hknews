package com.github.ayltai.hknews.data;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class AmazonDynamoDBFactory {
    @NotNull
    public AmazonDynamoDB create() {
        return AmazonDynamoDBFactory.create(System.getenv("AWS_DYNAMODB_URL"));
    }

    @NotNull
    public AmazonDynamoDB create(@Nullable final String endpoint) {
        final AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard();

        if (endpoint != null) builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, null));

        return builder.build();
    }
}
