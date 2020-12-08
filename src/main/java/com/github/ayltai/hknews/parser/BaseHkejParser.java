package com.github.ayltai.hknews.parser;

import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;

import org.apache.commons.lang3.StringUtils;

public abstract class BaseHkejParser extends Parser {
    protected static final String QUOTE = "\"";

    protected BaseHkejParser(@NonNull final String sourceName, @NonNull final SourceService sourceService, @NonNull final ContentServiceFactory contentServiceFactory) {
        super(sourceName, sourceService, contentServiceFactory);
    }

    protected static void processImages(@NonNull final String html, @NonNull final Item item) {
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
