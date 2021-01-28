package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.RssEnclosure;
import com.github.ayltai.hknews.data.model.RssFeed;
import com.github.ayltai.hknews.data.model.RssItem;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.model.Video;
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;

import org.jetbrains.annotations.NotNull;

public abstract class RssParser extends Parser {
    RssParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentServiceFactory contentServiceFactory, @NotNull final LambdaLogger logger) {
        super(sourceName, sourceService, contentServiceFactory, logger);
    }

    @NotNull
    @Override
    protected Collection<Item> getItems(@NotNull final Source source) throws ProtocolException, SSLHandshakeException, SocketTimeoutException, SSLException, IOException {
        final RssFeed feed = this.contentServiceFactory.create().getFeed(source.getUrl()).execute().body();
        return (feed == null || feed.getItems() == null || feed.getItems().isEmpty() ? Collections.<RssItem>emptyList() : feed.getItems())
            .stream()
            .filter(rssItem -> rssItem.getLink() != null)
            .map(rssItem -> {
                final Item item = new Item();

                item.setTitle(rssItem.getTitle() == null ? null : rssItem.getTitle().trim());
                item.setDescription(rssItem.getDescription() == null ? null : rssItem.getDescription().trim());
                item.setUrl(rssItem.getLink().trim());
                item.setPublishDate(Date.from(ZonedDateTime.parse(rssItem.getPubDate().trim(), DateTimeFormatter.RFC_1123_DATE_TIME).withZoneSameInstant(ZoneId.systemDefault()).toInstant()));
                item.setSourceName(source.getSourceName());
                item.setCategoryName(source.getCategoryName());

                RssParser.processImages(item, rssItem);
                RssParser.processVideos(item, rssItem);

                return item;
            })
            .collect(Collectors.toList());
    }

    private static void processImages(@NotNull final Item item, @NotNull final RssItem rssItem) {
        if (rssItem.getEnclosures() != null && !rssItem.getEnclosures().isEmpty()) item.getImages().addAll(rssItem.getEnclosures()
            .stream()
            .filter(RssEnclosure::isImage)
            .map(enclosure -> new Image(enclosure.getUrl(), null))
            .collect(Collectors.toList()));
    }

    private static void processVideos(@NotNull final Item item, @NotNull final RssItem rssItem) {
        if (rssItem.getEnclosures() != null && !rssItem.getEnclosures().isEmpty()) item.getVideos().addAll(rssItem.getEnclosures()
            .stream()
            .filter(RssEnclosure::isVideo)
            .map(enclosure -> new Video(enclosure.getUrl(), item.getImages().get(0).getUrl()))
            .collect(Collectors.toList()));
    }
}
