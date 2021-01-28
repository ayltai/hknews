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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HeadlineRealtimeParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeadlineRealtimeParser.class);

    private static final String QUOTE = "\"";

    public HeadlineRealtimeParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentServiceFactory contentServiceFactory) {
        super(sourceName, sourceService, contentServiceFactory);
    }

    @NotNull
    @Override
    public Collection<Item> getItems(@NotNull final String categoryName) {
        return this.sourceService
            .getSources(this.sourceName, categoryName)
            .stream()
            .map(source -> {
                try {
                    final String[] sections = StringUtils.substringsBetween(this.contentServiceFactory.create().getHtml(source.getUrl()).execute().body(), "<div class=\"topic\">", "<p class=\"text-left\">");
                    if (sections != null) return Stream.of(sections)
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
                } catch (final ProtocolException e) {
                    if (e.getMessage().startsWith("Too many follow-up requests")) HeadlineRealtimeParser.LOGGER.info(e.getMessage(), e);
                } catch (final SSLHandshakeException | SocketTimeoutException e) {
                    HeadlineRealtimeParser.LOGGER.info(e.getMessage(), e);
                } catch (final SSLException e) {
                    if (e.getMessage().equals("Connection reset")) HeadlineRealtimeParser.LOGGER.info(e.getMessage(), e);
                } catch (final IOException e) {
                    HeadlineRealtimeParser.LOGGER.error(this.getClass().getSimpleName(), e.getMessage(), e);
                }

                return Collections.<Item>emptyList();
            })
            .flatMap(List::stream)
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
