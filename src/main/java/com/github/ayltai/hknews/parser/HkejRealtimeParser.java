package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HkejRealtimeParser extends BaseHkejParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(HkejRealtimeParser.class);

    public HkejRealtimeParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentServiceFactory contentServiceFactory) {
        super(sourceName, sourceService, contentServiceFactory);
    }

    @NotNull
    @Override
    public Collection<Item> getItems(@NotNull final String categoryName) {
        final LocalDate now = LocalDate.now();

        return this.sourceService
            .getSources(this.sourceName, categoryName)
            .stream()
            .map(source -> {
                try {
                    final String[] sections = StringUtils.substringsBetween(this.contentServiceFactory.create().getHtml(source.getUrl()).execute().body(), "<h3>", "</div>");
                    if (sections != null) return Stream.of(sections)
                        .filter(Objects::nonNull)
                        .map(section -> {
                            final String url = StringUtils.substringBetween(section, "<a href=\"", BaseHkejParser.QUOTE);
                            if (url == null) return null;

                            String date = StringUtils.substringBetween(section, "<p class=\"hkej_toc_cat_top_timeStamp_2014\">今日 ", "</p>");
                            if (date == null) date = StringUtils.substringBetween(section, "<span class=\"hkej_toc_top2_timeStamp_2014\">", "</span>");
                            if (date == null) return null;

                            final Item item = new Item();
                            item.setTitle(StringUtils.substringBetween(section, "\">", "</a>"));
                            item.setUrl("https://www2.hkej.com" + url);
                            item.setPublishDate(Date.from(now.atTime(LocalTime.parse(date, DateTimeFormatter.ofPattern("HH:mm"))).atZone(ZoneId.systemDefault()).toInstant()));
                            item.setSourceName(source.getSourceName());
                            item.setCategoryName(source.getCategoryName());

                            return item;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                } catch (final ProtocolException e) {
                    if (e.getMessage().startsWith("Too many follow-up requests")) HkejRealtimeParser.LOGGER.info(e.getMessage(), e);
                } catch (final SSLHandshakeException | SocketTimeoutException e) {
                    HkejRealtimeParser.LOGGER.info(e.getMessage(), e);
                } catch (final SSLException e) {
                    if (e.getMessage().equals("Connection reset")) HkejRealtimeParser.LOGGER.info(e.getMessage(), e);
                } catch (final IOException e) {
                    HkejRealtimeParser.LOGGER.error(this.getClass().getSimpleName(), e.getMessage(), e);
                }

                return Collections.<Item>emptyList();
            })
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Item updateItem(@NotNull final Item item) throws IOException {
        final String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<div id=\"article-detail-wrapper\">", "<!-- 相關文章 start -->");
        if (html != null) {
            final String[] descriptions = StringUtils.substringsBetween(StringUtils.substringBetween(html, "<div id='article-content'>", "</div>"), "<p>", "</p>");
            if (descriptions != null) item.setDescription(String.join("<br><br>", descriptions));

            BaseHkejParser.processImages(html, item);
        }

        return item;
    }
}
