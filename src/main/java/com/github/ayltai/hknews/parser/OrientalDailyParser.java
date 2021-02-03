package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.model.Video;
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public final class OrientalDailyParser extends Parser {
    //region Constants

    private static final String BASE_URL = "https://orientaldaily.on.cc";
    private static final String QUOTE    = "\"";
    private static final String SLASH    = "/";
    private static final String CLOSE    = "</div>";

    //endregion

    public OrientalDailyParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentServiceFactory contentServiceFactory, @NotNull final LambdaLogger logger) {
        super(sourceName, sourceService, contentServiceFactory, logger);
    }

    @NotNull
    @Override
    protected Collection<Item> getItems(@NotNull final Source source) throws IOException {
        final LocalDate now = LocalDate.now();

        String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(String.format(source.getUrl(), now.format(DateTimeFormatter.ofPattern("yyyyMMdd")))).execute().body(), "<div id=\"articleList\">", "<!--//articleList-->");

        if ("國際".equals(source.getCategoryName())) html = StringUtils.substringBetween(html, "<h2>要聞</h2>", "<h2>兩岸</h2>");
        if ("兩岸".equals(source.getCategoryName())) html = StringUtils.substringBetween(html, "<h2>兩岸</h2>", OrientalDailyParser.CLOSE);

        final String[] sections = StringUtils.substringsBetween(html, "<li>", "</li>");
        if (sections == null) return Collections.emptyList();

        return Stream.of(sections)
            .filter(Objects::nonNull)
            .map(section -> {
                final Item item = new Item();
                item.setTitle(StringUtils.substringBetween(section, "\">", "<"));
                item.setUrl(OrientalDailyParser.BASE_URL + StringUtils.substringBetween(section, "<a href=\"", "\""));
                item.setPublishDate(Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                item.setSourceName(source.getSourceName());
                item.setCategoryName(source.getCategoryName());

                return item;
            })
            .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Item updateItem(@NotNull final Item item) throws IOException {
        final String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<div id=\"contentCTN-top\"", "</div><!--//contentCTN-->");
        if (html != null) {
            final String[] descriptions = StringUtils.substringsBetween(html.replace("<h3>", "<p>").replace("</h3>", "</p>"), "<p>", "</p>");
            if (descriptions != null) item.setDescription(String.join("<br><br>", descriptions));

            OrientalDailyParser.processImages(html, item);
            this.processVideos(item);
        }

        return item;
    }

    private static void processImages(@NotNull final String html, @NotNull final Item item) {
        final String[] imageContainers = StringUtils.substringsBetween(html, "<div class=\"photo", OrientalDailyParser.CLOSE);
        if (imageContainers != null) {
            item.getImages().clear();
            item.getImages().addAll(Stream.of(imageContainers)
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, "href=\"", OrientalDailyParser.QUOTE);
                    if (imageUrl == null) return null;

                    return new Image(OrientalDailyParser.BASE_URL + imageUrl, StringUtils.substringBetween(imageContainer, "title=\"", OrientalDailyParser.QUOTE));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        }
    }

    private void processVideos(@NotNull final Item item) throws IOException {
        final LocalDate date     = item.getPublishDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        final String    fullDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        final String    xml      = this.contentServiceFactory.create().getHtml(OrientalDailyParser.BASE_URL + "/cnt/keyinfo/" + fullDate + "/videolist.xml").execute().body();

        if (xml != null) {
            final String key = "odn-" + fullDate + "-" + date.format(DateTimeFormatter.ofPattern("MMdd")) + "_" + StringUtils.substringBetween(item.getUrl(), fullDate + OrientalDailyParser.SLASH, ".html");

            item.getVideos().clear();
            item.getVideos().addAll(Stream.of(StringUtils.substringsBetween(xml, "<news>", "</news>"))
                .filter(section -> key.equals(StringUtils.substringBetween(section, "<articleID>", "</articleID>")))
                .map(section -> {
                    final String videoUrl     = StringUtils.substringBetween(section, "<video_url>", "</video_url>");
                    final String thumbnailUrl = StringUtils.substringBetween(section, "<thumbnail>", "</thumbnail>");

                    if (videoUrl == null || thumbnailUrl == null) return null;

                    final String shortDate = date.format(DateTimeFormatter.ofPattern("yyyyMM"));

                    return new Video("https://video-cdn.on.cc/Video/" + shortDate + OrientalDailyParser.SLASH + StringUtils.substringBetween(videoUrl, "?mid=", "&amp;mtype=video") + "_ipad.mp4", "https://tv.on.cc/xml/Thumbnail/" + shortDate + "/bigthumbnail/" + thumbnailUrl);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        }
    }
}
