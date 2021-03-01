package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.net.ContentService;
import com.github.ayltai.hknews.service.SourceService;
import com.github.ayltai.hknews.util.StringUtils;

import org.jetbrains.annotations.NotNull;

public final class WenWeiPoParser extends Parser {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public WenWeiPoParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentService contentService, @NotNull final LambdaLogger logger) {
        super(sourceName, sourceService, contentService, logger);
    }

    @NotNull
    @Override
    protected Collection<Item> getItems(@NotNull final Source source) throws IOException {
        final String[] sections = StringUtils.substringsBetween(this.contentService.getHtml(source.getUrl()), "<div class=\"story-item ", "<div class=\"story-item-tag ");
        if (sections == null) return Collections.emptyList();

        return Arrays.stream(sections)
            .filter(Objects::nonNull)
            .map(section -> {
                final String url = StringUtils.substringBetween(section, "<a href=\"", "\"");
                if (url == null) return null;

                final Item item = new Item();
                item.setTitle(StringUtils.substringBetween(section, "title=\"", "\""));
                item.setUrl(url);
                item.setSourceName(source.getSourceName());
                item.setCategoryName(source.getCategoryName());

                return item;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Item updateItem(@NotNull final Item item) throws IOException {
        final String html = StringUtils.substringBetween(this.contentService.getHtml(item.getUrl()), "<main class=\"mainbody\">", "<div class=\"post-footer\">");
        if (html != null) {
            final String date = StringUtils.substringBetween(html, "<span class=\"publish-date\">", "</span>");
            if (date != null) item.setPublishDate(Date.from(ZonedDateTime.parse(date, DateTimeFormatter.ofPattern(WenWeiPoParser.DATE_FORMAT)).toInstant()));

            final String[] descriptions = StringUtils.substringsBetween(html, "<p>", "</p>");
            if (descriptions != null) item.setDescription(String.join("<br><br>", descriptions));

            WenWeiPoParser.processImages(html, item);
        }

        return item;
    }

    private static void processImages(@NotNull final String html, @NotNull final Item item) {
        final String[] imageContainers = StringUtils.substringsBetween(html, "<figure ", "</figure>");
        if (imageContainers != null) {
            item.getImages().clear();
            item.getImages().addAll(Arrays.stream(imageContainers)
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, "<img src=\"", "\"");
                    if (imageUrl == null) return null;

                    final String imageDescription = StringUtils.substringBetween(imageContainer, "<figcaption style=\"display: table-caption; caption-side: bottom;\">", "</figcaption>");

                    return new Image(imageUrl, imageDescription == null ? null : imageDescription.trim());
                })
                .collect(Collectors.toList()));
        }
    }
}
