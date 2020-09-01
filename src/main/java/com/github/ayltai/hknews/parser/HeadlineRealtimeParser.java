package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HeadlineRealtimeParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeadlineRealtimeParser.class);

    private static final String QUOTE = "\"";

    public HeadlineRealtimeParser(@NonNull final String sourceName, @NonNull final SourceService sourceService, @NonNull final ContentServiceFactory contentServiceFactory) {
        super(sourceName, sourceService, contentServiceFactory);
    }

    @NonNull
    @Override
    public Collection<Item> getItems(@NonNull final String categoryName) {
        return this.sourceService
            .getSources(this.sourceName)
            .stream()
            .filter(source -> source.getCategoryName().equals(categoryName))
            .map(Source::getUrl)
            .map(url -> {
                try {
                    return StringUtils.substringsBetween(this.contentServiceFactory.create().getHtml(url).execute().body(), "<div class=\"topic\">", "<p class=\"text-left\">");
                } catch (final ProtocolException e) {
                    if (e.getMessage().startsWith("Too many follow-up requests")) HeadlineRealtimeParser.LOGGER.info(e.getMessage(), e);
                } catch (final SSLHandshakeException | SocketTimeoutException e) {
                    HeadlineRealtimeParser.LOGGER.info(e.getMessage(), e);
                } catch (final SSLException e) {
                    if (e.getMessage().equals("Connection reset")) HeadlineRealtimeParser.LOGGER.info(e.getMessage(), e);
                } catch (final IOException e) {
                    HeadlineRealtimeParser.LOGGER.error(this.getClass().getSimpleName(), e.getMessage(), e);
                }

                return null;
            })
            .filter(Objects::nonNull)
            .flatMap(Stream::of)
            .map(section -> {
                final String url = StringUtils.substringBetween(section, "<a href=\"", HeadlineRealtimeParser.QUOTE);
                if (url == null) return null;

                final String date = StringUtils.substringBetween(section, "</i>", "</span>");
                if (date == null) return null;

                final Item item = new Item();
                item.setTitle(StringUtils.substringBetween(section, " title=\"", HeadlineRealtimeParser.QUOTE));
                item.setUrl("https://hd.stheadline.com" + url);
                item.setPublishDate(Date.from(LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).atZone(ZoneId.systemDefault()).toInstant()));
                item.setSourceName(this.sourceName);
                item.setCategoryName(categoryName);

                return item;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @NonNull
    @Override
    public Item updateItem(@NonNull final Item item) throws IOException {
        final String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<div class=\"content post-content\">", "</article>");
        if (html != null) {
            final String description = StringUtils.substringBetween(html, "<p>", "</p>");
            if (description != null) item.setDescription(description);

            HeadlineRealtimeParser.processImages(html, item);
        }

        return item;
    }

    private static void processImages(@NonNull final String html, @NonNull final Item item) {
        final String[] imageContainers = StringUtils.substringsBetween(html, "<figure", "</figure>");
        if (imageContainers != null) item.getImages().addAll(Stream.of(imageContainers)
            .map(imageContainer -> {
                final String imageUrl = StringUtils.substringBetween(imageContainer, "<a class=\"fancybox image\" rel=\"fancybox-thumb\" href=\"", HeadlineRealtimeParser.QUOTE);
                if (imageUrl == null) return null;

                return new Image(null, item, imageUrl, StringUtils.substringBetween(imageContainer, "<figcaption class=\"caption-text\">", "</figcaption>"));
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));
    }
}
