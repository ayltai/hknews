package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Category;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ContentServiceFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HkejRealtimeParser extends BaseHkejParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(HkejRealtimeParser.class);

    public HkejRealtimeParser(@NonNull final String sourceName, @NonNull final SourceRepository sourceRepository, @NonNull final ContentServiceFactory contentServiceFactory) {
        super(sourceName, sourceRepository, contentServiceFactory);
    }

    @NonNull
    @Override
    public Collection<Item> getItems(@NonNull final String categoryName) {
        final LocalDate now = LocalDate.now();

        return this.sourceRepository
            .findByName(this.sourceName)
            .getCategories()
            .stream()
            .filter(category -> category.getName().equals(categoryName))
            .map(Category::getUrls)
            .flatMap(List::stream)
            .map(url -> {
                try {
                    return StringUtils.substringsBetween(this.contentServiceFactory.create().getHtml(url).execute().body(), "<h3>", "</div>");
                } catch (final IOException e) {
                    HkejRealtimeParser.LOGGER.error(this.getClass().getSimpleName(), e.getMessage(), e);
                }

                return null;
            })
            .filter(Objects::nonNull)
            .flatMap(Stream::of)
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
        final String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<div id=\"article-detail-wrapper\">", "<!-- 相關文章 start -->");
        if (html != null) {
            final String[] descriptions = StringUtils.substringsBetween(StringUtils.substringBetween(html, "<div id='article-content'>", "</div>"), "<p>", "</p>");
            if (descriptions != null) item.setDescription(String.join("<br><br>", descriptions));

            BaseHkejParser.processImages(html, item);
        }

        return item;
    }
}
