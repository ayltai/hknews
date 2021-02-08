package com.github.ayltai.hknews.parser;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.net.ContentService;
import com.github.ayltai.hknews.service.SourceService;
import com.github.ayltai.hknews.util.StringUtils;

import org.jetbrains.annotations.NotNull;

public abstract class BaseHkejParser extends Parser {
    protected static final String QUOTE = "\"";

    protected BaseHkejParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentService contentService, @NotNull final LambdaLogger logger) {
        super(sourceName, sourceService, contentService, logger);
    }

    protected static void processImages(@NotNull final String html, @NotNull final Item item) {
        final String imageContainer = StringUtils.substringBetween(html, "<span class='enlargeImg'>", "</span>");
        if (imageContainer != null) {
            final String imageUrl = StringUtils.substringBetween(imageContainer, "<a href=\"", BaseHkejParser.QUOTE);
            if (imageUrl != null) {
                item.getImages().clear();
                item.getImages().add(new Image(imageUrl.startsWith("http") ? imageUrl : "https:" + imageUrl, StringUtils.substringBetween(imageContainer, "title=\"", BaseHkejParser.QUOTE)));
            }
        }
    }
}
