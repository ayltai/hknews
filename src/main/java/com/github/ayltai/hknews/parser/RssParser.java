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
    public Collection<Item> getItems(@NonNull final String categoryName) {
        return this.sourceService
            .getSources(this.sourceName, categoryName)
            .stream()
            .map(Source::getUrl)
            .map(url -> this.contentServiceFactory.create().getFeed(url))
            .map(call -> {
                try {
                    final RssFeed feed = call.execute().body();
                    return feed == null || feed.getItems() == null || feed.getItems().isEmpty() ? null : feed;
                } catch (final ProtocolException e) {
                    if (e.getMessage().startsWith("Too many follow-up requests")) RssParser.LOGGER.info(e.getMessage(), e);
                } catch (final SSLHandshakeException | SocketTimeoutException e) {
                    RssParser.LOGGER.info(e.getMessage(), e);
                } catch (final SSLException e) {
                    if (e.getMessage().equals("Connection reset")) RssParser.LOGGER.info(e.getMessage(), e);
                } catch (final IOException e) {
                    RssParser.LOGGER.warn(e.getMessage(), e);
                }

                return null;
            })
            .filter(Objects::nonNull)
            .map(RssFeed::getItems)
            .flatMap(Collection::stream)
            .filter(rssItem -> rssItem.getLink() != null)
            .map(rssItem -> {
                final Item item = new Item();

                item.setTitle(rssItem.getTitle() == null ? null : rssItem.getTitle().trim());
                item.setDescription(rssItem.getDescription() == null ? null : rssItem.getDescription().trim());
                item.setUrl(rssItem.getLink().trim());
                item.setPublishDate(Date.from(ZonedDateTime.parse(rssItem.getPubDate().trim(), DateTimeFormatter.RFC_1123_DATE_TIME).withZoneSameInstant(ZoneId.systemDefault()).toInstant()));
                item.setSourceName(this.sourceName);
                item.setCategoryName(categoryName);

                RssParser.processImages(item, rssItem);
                RssParser.processVideos(item, rssItem);

                return item;
            })
            .collect(Collectors.toList());
    }

    private static void processImages(@NonNull final Item item, @NonNull final RssItem rssItem) {
        if (rssItem.getEnclosures() != null && !rssItem.getEnclosures().isEmpty()) item.getImages().addAll(rssItem.getEnclosures()
            .stream()
            .filter(RssEnclosure::isImage)
            .map(enclosure -> new Image(enclosure.getUrl(), null))
            .collect(Collectors.toList()));
    }

    private static void processVideos(@NonNull final Item item, @NonNull final RssItem rssItem) {
        if (rssItem.getEnclosures() != null && !rssItem.getEnclosures().isEmpty()) item.getVideos().addAll(rssItem.getEnclosures()
            .stream()
            .filter(RssEnclosure::isVideo)
            .map(enclosure -> new Video(enclosure.getUrl(), item.getImages().get(0).getUrl()))
            .collect(Collectors.toList()));
    }
}
