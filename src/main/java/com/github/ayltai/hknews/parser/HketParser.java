package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;
import org.apache.commons.lang3.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HketParser extends RssParser {
    private static final String QUOTE = "\"";

    public HketParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentServiceFactory contentServiceFactory, @NotNull final LambdaLogger logger) {
        super(sourceName, sourceService, contentServiceFactory, logger);
    }

    @NotNull
    @Override
    public Item updateItem(@NotNull final Item item) throws IOException {
        final String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<div class=\"article-detail-body-container\">", "<div class=\"article-details-center-sharing-btn\">");
        if (html != null) {
            final String[] descriptions = StringUtils.substringsBetween(html, "<p>", "</p>");
            if (descriptions != null) item.setDescription(String.join("<br><br>", descriptions));

            HketParser.processImages(html, item);
        }

        return item;
    }

    private static void processImages(@NotNull final String html, @NotNull final Item item) {
        final String[] imageContainers = StringUtils.substringsBetween(html, "<img ", "/>");
        if (imageContainers != null) {
            item.getImages().clear();
            item.getImages().addAll(Stream.of(imageContainers)
                .map(HketParser::extractImage)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        }
    }

    @Nullable
    private static Image extractImage(@NotNull final String imageContainer) {
        final String imageUrl = StringUtils.substringBetween(imageContainer, "data-src=\"", HketParser.QUOTE);
        if (imageUrl == null) return null;

        return new Image(imageUrl, StringUtils.substringBetween(imageContainer, "data-alt=\"", HketParser.QUOTE));
    }
}
