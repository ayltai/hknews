package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
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

public final class SkyPostParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkyPostParser.class.getSimpleName());

    public SkyPostParser(@NonNull final String sourceName, @NonNull final SourceService sourceService, @NonNull final ContentServiceFactory contentServiceFactory) {
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
            .map(url -> this.contentServiceFactory.create().getHtml(url))
            .map(call -> {
                try {
                    return StringUtils.substringsBetween(StringUtils.substringBetween(call.execute().body(), "<section class=\"article-listing", "</section>"), "<h5 class='card-title'>", "<button class=\"share-container\"");
                } catch (final ProtocolException e) {
                    if (e.getMessage().startsWith("Too many follow-up requests")) SkyPostParser.LOGGER.info(e.getMessage(), e);
                } catch (final SSLHandshakeException | SocketTimeoutException e) {
                    SkyPostParser.LOGGER.info(e.getMessage(), e);
                } catch (final SSLException e) {
                    if (e.getMessage().equals("Connection reset")) SkyPostParser.LOGGER.info(e.getMessage(), e);
                } catch (final IOException e) {
                    SkyPostParser.LOGGER.error(this.getClass().getSimpleName(), e.getMessage(), e);
                }

                return null;
            })
            .filter(Objects::nonNull)
            .flatMap(Stream::of)
            .map(section -> {
                final String url = StringUtils.substringBetween(section , "<a href='", "'>");
                if (url == null) return null;

                final String[] dates = StringUtils.substringsBetween(section, "<span class='time'>", "</span>");
                if (dates == null) return null;

                final Item item = new Item();
                item.setTitle(StringUtils.substringBetween(section, "'>", "</a>"));
                item.setUrl(url);
                item.setPublishDate(Date.from(LocalDate.parse(dates[1], DateTimeFormatter.ofPattern("yyyy/MM/dd")).atStartOfDay(ZoneId.systemDefault()).toInstant()));
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

    private static void processImages(@NonNull final String html, @NonNull final Item item) {
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
