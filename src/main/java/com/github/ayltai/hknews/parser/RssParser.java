package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.RssEnclosure;
import com.github.ayltai.hknews.data.model.RssFeed;
import com.github.ayltai.hknews.data.model.RssItem;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.model.Video;
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RssParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(RssParser.class);

    RssParser(@NonNull final String sourceName, @NonNull final SourceService sourceService, @NonNull final ContentServiceFactory contentServiceFactory) {
        super(sourceName, sourceService, contentServiceFactory);
    }

    @NonNull
    @Override
    public final Collection<Item> getItems(@NonNull final String categoryName) {
        return this.sourceService
            .getSources(this.sourceName)
            .stream()
            .filter(source -> source.getCategoryName().equals(categoryName))
            .map(Source::getUrl)
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
        } catch (final ProtocolException e) {
            if (e.getMessage().startsWith("Too many follow-up requests")) RssParser.LOGGER.info(e.getMessage(), e);
        } catch (final SSLHandshakeException | SocketTimeoutException e) {
            RssParser.LOGGER.info(e.getMessage(), e);
        } catch (final SSLException e) {
            if (e.getMessage().equals("Connection reset")) RssParser.LOGGER.info(e.getMessage(), e);
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
                image.setItem(item);
                image.setUrl(enclosure.getUrl());

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
                    video.setItem(item);
                    video.setUrl(enclosure.getUrl());
                    video.setCover(item.getImages().get(0).getUrl());

                    item.getVideos().add(video);
                }
            });
    }
}
