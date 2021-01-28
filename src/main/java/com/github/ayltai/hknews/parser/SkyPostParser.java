package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
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

public final class SkyPostParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkyPostParser.class.getSimpleName());

    public SkyPostParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentServiceFactory contentServiceFactory) {
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
                    final String[] sections = StringUtils.substringsBetween(StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(source.getUrl()).execute().body(), "<section class=\"article-listing", "</section>"), "<h5 class='card-title'>", "<button class=\"share-container\"");
                    if (sections != null) return Stream.of(sections)
                        .filter(Objects::nonNull)
                        .map(section -> {
                            final String url = StringUtils.substringBetween(section , "<a href='", "'>");
                            if (url == null) return null;

                            final String[] dates = StringUtils.substringsBetween(section, "<span class='time'>", "</span>");
                            if (dates == null) return null;

                            final Item item = new Item();
                            item.setTitle(StringUtils.substringBetween(section, "'>", "</a>"));
                            item.setUrl(url);
                            item.setPublishDate(Date.from(LocalDate.parse(dates[1], DateTimeFormatter.ofPattern("yyyy/MM/dd")).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                            item.setSourceName(source.getSourceName());
                            item.setCategoryName(source.getCategoryName());

                            return item;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                } catch (final ProtocolException e) {
                    if (e.getMessage().startsWith("Too many follow-up requests")) SkyPostParser.LOGGER.info(e.getMessage(), e);
                } catch (final SSLHandshakeException | SocketTimeoutException e) {
                    SkyPostParser.LOGGER.info(e.getMessage(), e);
                } catch (final SSLException e) {
                    if (e.getMessage().equals("Connection reset")) SkyPostParser.LOGGER.info(e.getMessage(), e);
                } catch (final IOException e) {
                    SkyPostParser.LOGGER.error(this.getClass().getSimpleName(), e.getMessage(), e);
                }

                return Collections.<Item>emptyList();
            })
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Item updateItem(@NotNull final Item item) throws IOException {
        final String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<section class=\"article-head\">", "<div class=\"article-detail_extra-info\">");
        if (html != null) {
            final String[] descriptions = StringUtils.substringsBetween(html, "<P>", "</P>");
            if (descriptions != null) item.setDescription(Stream.of(descriptions)
                .map(description -> {
                    String text = StringUtils.substringBetween(description, "<b>", "</b>");
                    if (text == null) text = StringUtils.substringBetween(description, "<B>", "</B>");

                    return text == null || text.isEmpty() ? description : "<h4>" + text + "</h4>";
                })
                .collect(Collectors.joining("<br>")));

            SkyPostParser.processImages(html, item);
        }

        return item;
    }

    private static void processImages(@NotNull final String html, @NotNull final Item item) {
        final String[] imageContainers = StringUtils.substringsBetween(html, "<p class=\"article-details-img-container\">", "</div>");
        if (imageContainers != null) {
            item.getImages().clear();
            item.getImages().addAll(Stream.of(imageContainers)
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, "data-src=\"", "\"");
                    if (imageUrl == null) return null;

                    return new Image(imageUrl, StringUtils.substringBetween(imageContainer, "<p class=\"article-details-img-caption\">", "</p>"));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        }
    }
}
