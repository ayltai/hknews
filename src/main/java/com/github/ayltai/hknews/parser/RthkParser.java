package com.github.ayltai.hknews.parser;

import java.io.IOException;

import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Video;
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public final class RthkParser extends RssParser {
    public RthkParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentServiceFactory contentServiceFactory) {
        super(sourceName, sourceService, contentServiceFactory);
    }

    @NotNull
    @Override
    public Item updateItem(@NotNull final Item item) throws IOException {
        final String html = this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body();
        if (html != null) {
            final String description = StringUtils.substringBetween(html, "<div class=\"itemFullText\">", "</div>");
            if (description != null) item.setDescription(description.trim());

            RthkParser.processImagesAndVideos(html, item);
        }

        return item;
    }

    private static void processImagesAndVideos(@NotNull final String html, @NotNull final Item item) {
        final String imageContainer = StringUtils.substringBetween(html, "<div class=\"itemSlideShow\">", "<div class=\"clr\"></div>");
        if (imageContainer != null) {
            final String imageUrl = StringUtils.substringBetween(imageContainer, "<a href=\"", "\"");
            if (imageUrl != null) item.getImages().add(new Image(imageUrl, StringUtils.substringBetween(imageContainer, "alt=\"", "\"")));

            final String videoUrl     = StringUtils.substringBetween(imageContainer, "var videoFile\t\t\t= '", "'");
            final String thumbnailUrl = StringUtils.substringBetween(imageContainer, "var videoThumbnail\t\t= '", "'");

            if (videoUrl != null && thumbnailUrl != null) {
                item.getVideos().clear();
                item.getVideos().add(new Video(videoUrl, thumbnailUrl));
            }
        }
    }
}
