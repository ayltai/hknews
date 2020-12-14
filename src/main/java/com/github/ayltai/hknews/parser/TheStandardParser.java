package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;
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

public final class TheStandardParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(TheStandardParser.class);

    //region Constants

    private static final String CLOSE_QUOTE     = "\"";
    private static final String OPEN_HREF       = "<a href=\"";
    private static final String OPEN_PARAGRAPH  = "<p>";
    private static final String CLOSE_PARAGRAPH = "</p>";

    private static final String FORMAT_LONG  = "d MMM yyyy h:mm a";
    private static final String FORMAT_SHORT = "d MMM yyyy";

    //endregion

    public TheStandardParser(@NonNull final String sourceName, @NonNull final SourceService sourceService, @NonNull final ContentServiceFactory contentServiceFactory) {
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
                final String[] tokens = url.split(Pattern.quote("?"));

                try {
                    return StringUtils.substringsBetween(this.contentServiceFactory.create().postHtml(tokens[0], Integer.parseInt(tokens[1].split("=")[1]), 1).execute().body(), "<li class='caption'>", "</li>");
                } catch (final ProtocolException e) {
                    if (e.getMessage().startsWith("Too many follow-up requests")) TheStandardParser.LOGGER.info(e.getMessage(), e);
                } catch (final SSLHandshakeException | SocketTimeoutException e) {
                    TheStandardParser.LOGGER.info(e.getMessage(), e);
                } catch (final SSLException e) {
                    if (e.getMessage().equals("Connection reset")) TheStandardParser.LOGGER.info(e.getMessage(), e);
                } catch (final IOException e) {
                    TheStandardParser.LOGGER.error(this.getClass().getSimpleName(), e.getMessage(), e);
                }

                return null;
            })
            .filter(Objects::nonNull)
            .flatMap(Stream::of)
            .map(section -> {
                final String url = StringUtils.substringBetween(section, TheStandardParser.OPEN_HREF, TheStandardParser.CLOSE_QUOTE);
                if (url == null) return null;

                final String date = StringUtils.substringBetween(section, "<span>", "</span>");
                if (date == null) return null;

                final Item item = new Item();
                item.setTitle(StringUtils.substringBetween(StringUtils.substringBetween(section, "<h1>", "</h1>"), "\">", "</a>").trim());
                item.setDescription(StringUtils.substringBetween(section, TheStandardParser.OPEN_PARAGRAPH, TheStandardParser.CLOSE_PARAGRAPH));
                item.setUrl(url);
                item.setPublishDate(Date.from(date.length() > TheStandardParser.FORMAT_SHORT.length() + 1 ? LocalDateTime.parse(date, new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(TheStandardParser.FORMAT_LONG).toFormatter()).atZone(ZoneId.systemDefault()).toInstant() : LocalDate.parse(date, DateTimeFormatter.ofPattern(TheStandardParser.FORMAT_SHORT)).atStartOfDay(ZoneId.systemDefault()).toInstant()));
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
        final String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<div class=\"content\">", "<div class=\"related\">");
        if (html != null) {
            final String[] descriptions = StringUtils.substringsBetween(html, TheStandardParser.OPEN_PARAGRAPH, TheStandardParser.CLOSE_PARAGRAPH);
            if (descriptions != null) item.setDescription(String.join("<br><br>", descriptions));

            TheStandardParser.processImages(html, item);
        }

        return item;
    }

    private static void processImages(@NonNull final String html, @NonNull final Item item) {
        final String[] imageContainers = StringUtils.substringsBetween(html, "<figure>", "</figure>");
        if (imageContainers != null) {
            item.getImages().clear();
            item.getImages().addAll(Stream.of(imageContainers)
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, TheStandardParser.OPEN_HREF, TheStandardParser.CLOSE_QUOTE);
                    if (imageUrl == null) return null;

                    return new Image(item, imageUrl, StringUtils.substringBetween(imageContainer, "<i>", "</i>"));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        }
    }
}
