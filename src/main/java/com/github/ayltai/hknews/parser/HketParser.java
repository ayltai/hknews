package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;

import org.apache.commons.lang3.StringUtils;

public final class HketParser extends RssParser {
    private static final String QUOTE = "\"";

    public HketParser(@NonNull final String sourceName, @NonNull final SourceService sourceService, @NonNull final ContentServiceFactory contentServiceFactory) {
        super(sourceName, sourceService, contentServiceFactory);
    }

    @NonNull
    @Override
    public Item updateItem(@NonNull final Item item) throws IOException {
        final String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<div class=\"article-detail-body-container\">", "<div class=\"article-details-center-sharing-btn\">");
        if (html != null) {
            final String[] descriptions = StringUtils.substringsBetween(html, "<p>", "</p>");
            if (descriptions != null) item.setDescription(String.join("<br><br>", descriptions));

            HketParser.processImages(html, item);
        }

        return item;
    }

    private static void processImages(@NonNull final String html, @NonNull final Item item) {
        final String[] imageContainers = StringUtils.substringsBetween(html, "<img ", "/>");
        if (imageContainers != null) {
            item.getImages().clear();
            item.getImages().addAll(Stream.of(imageContainers)
                .map(imageContainer -> HketParser.extractImage(imageContainer, item))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        }
    }

    @Nullable
    private static Image extractImage(@NonNull @lombok.NonNull final String imageContainer, @NonNull final Item item) {
        final String imageUrl = StringUtils.substringBetween(imageContainer, "data-src=\"", HketParser.QUOTE);
        if (imageUrl == null) return null;

        return new Image(null, item, imageUrl, StringUtils.substringBetween(imageContainer, "data-alt=\"", HketParser.QUOTE));
    }
}
