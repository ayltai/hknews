package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public final class HeadlineRealtimeParser extends Parser {
    private static final String QUOTE = "\"";

    public HeadlineRealtimeParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentServiceFactory contentServiceFactory, @NotNull final LambdaLogger logger) {
        super(sourceName, sourceService, contentServiceFactory, logger);
    }

    @NotNull
    @Override
    protected Collection<Item> getItems(@NotNull final Source source) throws ProtocolException, SSLHandshakeException, SocketTimeoutException, SSLException, IOException {
        final String[] sections = StringUtils.substringsBetween(this.contentServiceFactory.create().getHtml(source.getUrl()).execute().body(), "<div class=\"topic\">", "<p class=\"text-left\">");
        if (sections == null) return Collections.emptyList();

        return Stream.of(sections)
            .filter(Objects::nonNull)
            .map(section -> {
                final String url = StringUtils.substringBetween(section, "<a href=\"", HeadlineRealtimeParser.QUOTE);
                if (url == null) return null;

                final String date = StringUtils.substringBetween(section, "</i>", "</span>");
                if (date == null) return null;

                final Item item = new Item();
                item.setTitle(StringUtils.substringBetween(section, " title=\"", HeadlineRealtimeParser.QUOTE));
                item.setUrl("https://hd.stheadline.com" + url);
                item.setPublishDate(Date.from(LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).atZone(ZoneId.systemDefault()).toInstant()));
                item.setSourceName(source.getSourceName());
                item.setCategoryName(source.getCategoryName());

                return item;
            })
            .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Item updateItem(@NotNull final Item item) throws IOException {
        final String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<div class=\"content post-content\">", "</article>");
        if (html != null) {
            final String description = StringUtils.substringBetween(html, "<p>", "</p>");
            if (description != null) item.setDescription(description);

            HeadlineRealtimeParser.processImages(html, item);
        }

        return item;
    }

    private static void processImages(@NotNull final String html, @NotNull final Item item) {
        final String[] imageContainers = StringUtils.substringsBetween(html, "<figure", "</figure>");
        if (imageContainers != null) {
            item.getImages().clear();
            item.getImages().addAll(Stream.of(imageContainers)
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, "<a class=\"fancybox image\" rel=\"fancybox-thumb\" href=\"", HeadlineRealtimeParser.QUOTE);
                    if (imageUrl == null) return null;

                    return new Image(imageUrl, StringUtils.substringBetween(imageContainer, "<figcaption class=\"caption-text\">", "</figcaption>"));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        }
    }
}
