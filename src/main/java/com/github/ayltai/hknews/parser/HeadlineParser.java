package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
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

public final class HeadlineParser extends Parser {
    private static final String QUOTE = "\"";

    public HeadlineParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentServiceFactory contentServiceFactory, @NotNull final LambdaLogger logger) {
        super(sourceName, sourceService, contentServiceFactory, logger);
    }

    @NotNull
    @Override
    protected Collection<Item> getItems(@NotNull final Source source) throws ProtocolException, SSLHandshakeException, SocketTimeoutException, SSLException, IOException {
        final String[] sections = StringUtils.substringsBetween(this.contentServiceFactory.create().getHtml(source.getUrl()).execute().body(), "<div class=\"topic\">", "<p class=\"text-left\">");
        if (sections == null) return Collections.emptyList();

        return Arrays.stream(sections)
            .filter(Objects::nonNull)
            .map(section -> {
                final String url = StringUtils.substringBetween(section, "<a href=\"", HeadlineParser.QUOTE);
                if (url == null) return null;

                final String date = StringUtils.substringBetween(section, "</i>", "</span>");
                if (date == null) return null;

                final Item item = new Item();
                item.setTitle(StringUtils.substringBetween(section, " title=\"", HeadlineParser.QUOTE).trim());
                item.setUrl("https://hd.stheadline.com" + url);
                item.setPublishDate(Date.from(LocalDate.parse(date).atStartOfDay(ZoneId.systemDefault()).toInstant()));
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
        final String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<div class=\"content", "<div class=\"content-fb-landing\">");
        if (html != null) {
            final String description = StringUtils.substringBetween(html, "<div id=\"news-content\" class=\"set-font-aera\" style=\"visibility: visible;\">", "</div>");
            if (description != null) item.setDescription(description);

            HeadlineParser.processImages(html, item);
        }

        return item;
    }

    private static void processImages(@NotNull final String html, @NotNull final Item item) {
        final String[] imageContainers = StringUtils.substringsBetween(html, "<div class=\"item", "</div>");
        if (imageContainers != null) {
            item.getImages().clear();
            item.getImages().addAll(Stream.of(imageContainers)
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, "href=\"", HeadlineParser.QUOTE);
                    if (imageUrl == null) return null;

                    return new Image(imageUrl, StringUtils.substringBetween(imageContainer, " title=\"■", HeadlineParser.QUOTE));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        }
    }
}
