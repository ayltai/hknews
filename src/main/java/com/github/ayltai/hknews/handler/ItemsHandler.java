package com.github.ayltai.hknews.handler;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.ayltai.hknews.Configuration;
import com.github.ayltai.hknews.data.AmazonDynamoDBFactory;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Page;
import com.github.ayltai.hknews.data.model.Pageable;
import com.github.ayltai.hknews.data.model.Sort;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.service.ItemService;

import org.jetbrains.annotations.NotNull;

public final class ItemsHandler extends ApiHandler {
    @NotNull
    @Override
    public APIGatewayProxyResponseEvent handleRequest(@NotNull final APIGatewayProxyRequestEvent event, @NotNull final Context context) {
        final Object response = this.getCache(event, context);
        if (response == null) {
            final Map<String, String> parameters    = event.getPathParameters();
            final Map<String, String> queries       = event.getQueryStringParameters();
            final String              sourceNames   = URLDecoder.decode(parameters.get("sourceNames"), StandardCharsets.UTF_8);
            final String              categoryNames = URLDecoder.decode(parameters.get("categoryNames"), StandardCharsets.UTF_8);
            final String              days          = parameters.get("days");
            final int                 pageNumber    = Integer.parseInt(queries.getOrDefault("pageNumber", String.valueOf(Configuration.DEFAULT.getPageNumber())));
            final int                 pageSize      = Integer.parseInt(queries.getOrDefault("pageSize", String.valueOf(Configuration.DEFAULT.getPageSize())));

            if (sourceNames == null || categoryNames == null || days == null) return APIGatewayProxyResponseEventFactory.badRequest();

            try {
                final Collection<Item> items      = new ItemService(new ItemRepository(AmazonDynamoDBFactory.create(), context.getLogger()), context.getLogger()).getItems(Arrays.asList(sourceNames.split(",")), Arrays.asList(categoryNames.split(",")), Integer.parseInt(days));
                final Collection<Item> pagedItems = items.stream().skip(pageSize * (pageNumber - 1L)).limit(pageSize).collect(Collectors.toList());
                final Sort             sort       = new Sort(true, false, items.isEmpty());
                final Pageable         pageable   = new Pageable(sort, pageSize * (pageNumber - 1), pageSize, pageNumber, false, true);
                final int              totalPages = items.isEmpty() ? 0 : (int)Math.ceil(items.size() / (double)pageable.getPageSize());
                final boolean          isEmpty    = items.isEmpty() || pageable.getPageNumber() < 1 || pageable.getPageNumber() > totalPages;
                final Page<Item>       page       = new Page<>(pageable, sort, pageable.getPageSize(), totalPages, items.size(), pagedItems.size(), pageable.getPageNumber() == 1, pageable.getPageNumber() == totalPages, isEmpty, pagedItems);

                this.putCache(event, context, page);

                return APIGatewayProxyResponseEventFactory.ok(page);
            } catch (final InterruptedException e) {
                this.log(e, event, context);
            }

            return APIGatewayProxyResponseEventFactory.error();
        }

        return APIGatewayProxyResponseEventFactory.ok(response);
    }
}
