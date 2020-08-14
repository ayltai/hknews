package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
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

public final class SkyPostParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkyPostParser.class.getSimpleName());

    public SkyPostParser(@NonNull final String sourceName, @NonNull final SourceRepository sourceRepository, @NonNull final ContentServiceFactory contentServiceFactory) {
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
                    return StringUtils.substringsBetween(StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(url).execute().body(), "<section class=\"article-listing", "</section>"), "<h5 class='card-title'>", "<button class=\"share-container\"");
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
        if (imageContainers != null) item.getImages().addAll(Stream.of(imageContainers)
            .map(imageContainer -> {
                final String imageUrl = StringUtils.substringBetween(imageContainer, "data-src=\"", "\"");
                if (imageUrl == null) return null;

                return new Image(imageUrl, StringUtils.substringBetween(imageContainer, "<p class=\"article-details-img-caption\">", "</p>"));
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));
    }
}
