package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Category;
import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.RssEnclosure;
import com.github.ayltai.hknews.data.model.RssFeed;
import com.github.ayltai.hknews.data.model.RssItem;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.model.Video;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ContentServiceFactory;

import org.slf4j.LoggerFactory;

public abstract class RssParser extends Parser {
    RssParser(@NonNull final String sourceName, @NonNull final SourceRepository sourceRepository, @NonNull final ContentServiceFactory contentServiceFactory) {
        super(sourceName, sourceRepository, contentServiceFactory);
    }

    @NonNull
    @Override
    public final Collection<Item> getItems(@NonNull final String categoryName) {
        final Source source = this.sourceRepository.findByName(this.sourceName);

        return source == null ? Collections.emptyList() : source.getCategories()
            .stream()
            .filter(category -> category.getName().equals(categoryName))
            .map(Category::getUrls)
            .flatMap(List::stream)
            .map(this::getFeed)
            .filter(Objects::nonNull)
            .map(RssFeed::getItems)
            .flatMap(Collection::stream)
            .filter(rssItem -> rssItem.getLink() != null)
            .map(rssItem -> this.getItem(rssItem, categoryName))
            .collect(Collectors.toList());
    }

    private RssFeed getFeed(@NonNull final String url) {
        try {
            final RssFeed feed = this.contentServiceFactory
                .create()
                .getFeed(url)
                .execute()
                .body();

            return feed == null || feed.getItems() == null || feed.getItems().isEmpty() ? null : feed;
        } catch (final IOException e) {
            LoggerFactory.getLogger(this.getClass()).warn(e.getMessage(), e);
        }

        return null;
    }

    private Item getItem(@NonNull final RssItem rssItem, @NonNull final String categoryName) {
        final Item item = new Item();
        item.setTitle(rssItem.getTitle().trim());
        item.setDescription(rssItem.getDescription().trim());
        item.setUrl(rssItem.getLink().trim());
        item.setPublishDate(Date.from(ZonedDateTime.parse(rssItem.getPubDate().trim(), DateTimeFormatter.RFC_1123_DATE_TIME).withZoneSameInstant(ZoneId.systemDefault()).toInstant()));
        item.setSourceName(this.sourceName);
        item.setCategoryName(categoryName);

        RssParser.processImages(item, rssItem);
        RssParser.processVideos(item, rssItem);

        return item;
    }

    private static void processImages(@NonNull final Item item, @NonNull final RssItem rssItem) {
        if (rssItem.getEnclosures() != null && !rssItem.getEnclosures().isEmpty()) rssItem.getEnclosures()
            .stream()
            .filter(RssEnclosure::isImage)
            .forEach(enclosure -> {
                final Image image = new Image();
                image.setImageUrl(enclosure.getUrl());

                item.getImages().add(image);
            });
    }

    private static void processVideos(@NonNull final Item item, @NonNull final RssItem rssItem) {
        if (rssItem.getEnclosures() != null && !rssItem.getEnclosures().isEmpty()) rssItem.getEnclosures()
            .stream()
            .filter(RssEnclosure::isVideo)
            .forEach(enclosure -> {
                if (!item.getImages().isEmpty()) {
                    final Video video = new Video();
                    video.setImageUrl(item.getImages().get(0).getImageUrl());
                    video.setVideoUrl(enclosure.getUrl());

                    item.getVideos().add(video);
                }
            });
    }
}
