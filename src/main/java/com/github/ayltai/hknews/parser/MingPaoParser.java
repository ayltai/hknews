package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Video;
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public final class MingPaoParser extends RssParser {
    private static final String DIV_CLEAR = "<div class=\"clear\"></div>";
    private static final String QUOTE     = "\"";

    public MingPaoParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentServiceFactory contentServiceFactory, @NotNull final LambdaLogger logger) {
        super(sourceName, sourceService, contentServiceFactory, logger);
    }

    @NotNull
    @Override
    public Item updateItem(@NotNull final Item item) throws IOException {
        final String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<hgroup>", "<h3>上 / 下一篇新聞</h3>");
        if (html != null) {
            item.setDescription(StringUtils.substringBetween(html, "<div id=\"upper\">", "</div>"));
            item.setDescription((item.getDescription() == null ? "" : item.getDescription() + "<br><br>") + StringUtils.substringBetween(html, "<div class=\"articlelogin\">", "</div>"));

            MingPaoParser.processImages(html, item);
            MingPaoParser.processVideos(html, item);
        }

        return item;
    }

    private static void processImages(@NotNull final String html, @NotNull final Item item) {
        final String[] imageContainers = StringUtils.substringsBetween(html, "id=\"zoom_", MingPaoParser.DIV_CLEAR);
        if (imageContainers != null) {
            item.getImages().clear();
            item.getImages().addAll(Stream.of(imageContainers)
                .filter(imageContainer -> !imageContainer.startsWith("video"))
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, "a href=\"", MingPaoParser.QUOTE);
                    if (imageUrl == null) return null;

                    return new Image(imageUrl, StringUtils.substringBetween(imageContainer, "dtitle=\"", MingPaoParser.QUOTE));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        }
    }

    private static void processVideos(@NotNull final String html, @NotNull final Item item) {
        final String[] videoContainers = StringUtils.substringsBetween(html, "id=\"zoom_video_", MingPaoParser.DIV_CLEAR);
        if (videoContainers != null) {
            item.getVideos().clear();
            item.getVideos().addAll(Stream.of(videoContainers)
                .map(videoContainer -> {
                    final String videoUrl = StringUtils.substringBetween(videoContainer, "<a href=\"https://videop.mingpao.com/php/player1.php?file=", "&");
                    if (videoUrl == null) return null;

                    return new Video(videoUrl, videoUrl.replaceAll(".mp4", ".jpg"));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        }
    }
}
