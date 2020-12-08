package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
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

public final class SingPaoParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingPaoParser.class);

    //region Constants

    private static final String BASE_URI  = "https://www.singpao.com.hk/";
    private static final String QUOTE     = "'";
    private static final String FONT      = "</font>";
    private static final String PARAGRAPH = "</p>";

    //endregion

    public SingPaoParser(@NonNull final String sourceName, @NonNull final SourceService sourceService, @NonNull final ContentServiceFactory contentServiceFactory) {
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
                    return StringUtils.substringsBetween(call.execute().body(), "<tr valign='top'><td width='220'>", "</td></tr>");
                } catch (final ProtocolException e) {
                    if (e.getMessage().startsWith("Too many follow-up requests")) SingPaoParser.LOGGER.info(e.getMessage(), e);
                } catch (final SSLHandshakeException | SocketTimeoutException e) {
                    SingPaoParser.LOGGER.info(e.getMessage(), e);
                } catch (final SSLException e) {
                    if (e.getMessage().equals("Connection reset")) SingPaoParser.LOGGER.info(e.getMessage(), e);
                } catch (final IOException e) {
                    SingPaoParser.LOGGER.warn(e.getMessage(), e);
                }

                return null;
            })
            .filter(Objects::nonNull)
            .flatMap(Stream::of)
            .map(section -> {
                final String url = StringUtils.substringBetween(section, "<td><a href='", SingPaoParser.QUOTE);
                if (url == null) return null;

                final String date = StringUtils.substringBetween(section, "<font class='list_date'>", "<br>");
                if (date == null) return null;

                final Item item = new Item();
                item.setTitle(StringUtils.substringBetween(section, "class='list_title'>", "</a>").trim());
                item.setDescription(StringUtils.substringBetween(section, "<br><br>\n", SingPaoParser.FONT));
                item.setUrl(SingPaoParser.BASE_URI + url);
                item.setPublishDate(Date.from(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay(ZoneId.systemDefault()).toInstant()));
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
        final String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<td class='news_title'>", "您可能有興趣:");
        if (html != null) {
            String[] descriptions = StringUtils.substringsBetween(html, "<p class=\"內文\">", SingPaoParser.PARAGRAPH);
            if (descriptions == null) descriptions = StringUtils.substringsBetween(html, "<p>", SingPaoParser.PARAGRAPH);
            if (descriptions != null) item.setDescription(String.join("<br><br>", descriptions));

            SingPaoParser.processImages(html, item);
        }

        return item;
    }

    private static void processImages(@NonNull final String html, @NonNull final Item item) {
        final String[] imageUrls = StringUtils.substringsBetween(html, "target='_blank'><img src='", SingPaoParser.QUOTE);
        final String[] imageDescriptions = StringUtils.substringsBetween(html, "<font size='4'>", SingPaoParser.FONT);

        if (imageUrls != null && imageUrls.length > 0) {
            for (int i = 0; i < imageUrls.length; i++) item.getImages().add(new Image(SingPaoParser.BASE_URI + imageUrls[i], imageDescriptions == null ? null : imageDescriptions.length > i ? imageDescriptions[i] : null));
        }

        final List<Image> images = item.getImages()
            .stream()
            .distinct()
            .collect(Collectors.toList());

        item.getImages().clear();
        item.getImages().addAll(images);
    }
}
