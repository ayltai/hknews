package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Category;
import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ContentServiceFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SingTaoParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingTaoParser.class);

    private static final String QUOTE = "\"";

    public SingTaoParser(@NonNull final String sourceName, @NonNull final SourceRepository sourceRepository, @NonNull final ContentServiceFactory contentServiceFactory) {
        super(sourceName, sourceRepository, contentServiceFactory);
    }

    @NonNull
    @Override
    public Collection<Item> getItems(@NonNull final String categoryName) {
        return this.sourceRepository
            .findByName(this.sourceName)
            .getCategories()
            .stream()
            .filter(category -> category.getName().equals(categoryName))
            .map(Category::getUrls)
            .flatMap(List::stream)
            .map(url -> {
                try {
                    return StringUtils.substringsBetween(this.contentServiceFactory.create().getHtml(url).execute().body(), "<div class=\"thumbnail\">", "an>");
                } catch (final IOException e) {
                    SingTaoParser.LOGGER.error(this.getClass().getSimpleName(), e.getMessage(), e);
                }

                return null;
            })
            .filter(Objects::nonNull)
            .flatMap(Stream::of)
            .map(section -> {
                final String url = StringUtils.substringBetween(section, "<a class=\"title\" href=\"", SingTaoParser.QUOTE);
                if (url == null) return null;

                final String date = StringUtils.substringBetween(section, "<span class=\"date\">", "</sp");
                if (date == null) return null;

                final Item item = new Item();
                item.setTitle(StringUtils.substringBetween(section, " title=\"", SingTaoParser.QUOTE));
                item.setUrl(url);
                item.setPublishDate(Date.from(LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()).toInstant()));
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
        final String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<article class=\"content\">", "</article>");
        if (html != null) {
            String description = StringUtils.substringBetween(html, "(星島日報報道)", "</div>");
            if (description == null) description = StringUtils.substringBetween(html, "<p>", "</p>");
            if (description != null) item.setDescription(description);

            SingTaoParser.processImages(html, item);
        }

        return item;
    }

    private static void processImages(@NonNull final String html, @NonNull final Item item) {
        final String[] imageContainers = StringUtils.substringsBetween(html, "<a class=\"fancybox-thumb", ">");
        if (imageContainers != null) item.getImages().addAll(Stream.of(imageContainers)
            .map(imageContainer -> {
                final String imageUrl = StringUtils.substringBetween(imageContainer, "href=\"", SingTaoParser.QUOTE);
                if (imageUrl == null) return null;

                return new Image(imageUrl, StringUtils.substringBetween(imageContainer, "title=\"", SingTaoParser.QUOTE));
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));
    }
}
