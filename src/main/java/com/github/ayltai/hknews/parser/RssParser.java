package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.model.Video;
import com.github.ayltai.hknews.data.model.rss.Enclosure;
import com.github.ayltai.hknews.data.model.rss.Entry;
import com.github.ayltai.hknews.data.model.rss.Root;
import com.github.ayltai.hknews.net.ContentService;
import com.github.ayltai.hknews.service.SourceService;

import org.jetbrains.annotations.NotNull;

public abstract class RssParser extends Parser {
    RssParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentService contentService, @NotNull final LambdaLogger logger) {
        super(sourceName, sourceService, contentService, logger);
    }

    @NotNull
    @Override
    protected Collection<Item> getItems(@NotNull final Source source) throws IOException {
        final Root rss = this.contentService.getRss(source.getUrl());
        return (rss.getChannel().getItems() == null || rss.getChannel().getItems().isEmpty()) ? Collections.emptyList() : rss.getChannel().getItems()
            .stream()
            .filter(entry -> entry.getLink() != null)
            .map(entry -> {
                final Item item = new Item();

                item.setTitle(entry.getTitle() == null ? null : entry.getTitle().trim());
                item.setDescription(entry.getDescription() == null ? null : entry.getDescription().trim());
                item.setUrl(entry.getLink().trim());
                item.setPublishDate(Date.from(ZonedDateTime.parse(entry.getPubDate().trim(), DateTimeFormatter.RFC_1123_DATE_TIME).toInstant()));
                item.setSourceName(source.getSourceName());
                item.setCategoryName(source.getCategoryName());

                RssParser.processImages(item, entry);
                RssParser.processVideos(item, entry);

                return item;
            })
            .collect(Collectors.toList());
    }

    private static void processImages(@NotNull final Item item, @NotNull final Entry entry) {
        if (entry.getEnclosures() != null && !entry.getEnclosures().isEmpty()) item.getImages().addAll(entry.getEnclosures()
            .stream()
            .filter(Enclosure::isImage)
            .map(enclosure -> new Image(enclosure.getUrl(), null))
            .collect(Collectors.toList()));
    }

    private static void processVideos(@NotNull final Item item, @NotNull final Entry entry) {
        if (entry.getEnclosures() != null && !entry.getEnclosures().isEmpty()) item.getVideos().addAll(entry.getEnclosures()
            .stream()
            .filter(Enclosure::isVideo)
            .map(enclosure -> new Video(enclosure.getUrl(), item.getImages().get(0).getUrl()))
            .collect(Collectors.toList()));
    }
}
