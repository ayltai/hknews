package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.Configuration;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.net.ContentService;
import com.github.ayltai.hknews.service.SourceService;
import com.github.ayltai.hknews.util.StringUtils;

import org.jetbrains.annotations.NotNull;

public final class HkejRealtimeParser extends BaseHkejParser {
    public HkejRealtimeParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentService contentService, @NotNull final LambdaLogger logger) {
        super(sourceName, sourceService, contentService, logger);
    }

    @NotNull
    @Override
    protected Collection<Item> getItems(@NotNull final Source source) throws IOException {
        final ZonedDateTime now = LocalDateTime.now().atZone(ZoneId.of(Configuration.DEFAULT.getTimeZone()));

        final String[] sections = StringUtils.substringsBetween(this.contentService.getHtml(source.getUrl()), "<h3>", "</div>");
        if (sections == null) return Collections.emptyList();

        return Stream.of(sections)
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
                item.setPublishDate(Date.from(now.with(LocalTime.parse(date, DateTimeFormatter.ofPattern("HH:mm"))).toInstant()));
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
        final String html = StringUtils.substringBetween(this.contentService.getHtml(item.getUrl()), "<div id=\"article-detail-wrapper\">", "<!-- 相關文章 start -->");
        if (html != null) {
            final String[] descriptions = StringUtils.substringsBetween(StringUtils.substringBetween(html, "<div id='article-content'>", "</div>"), "<p>", "</p>");
            if (descriptions != null) item.setDescription(String.join("<br><br>", descriptions));

            BaseHkejParser.processImages(html, item);
        }

        return item;
    }
}
