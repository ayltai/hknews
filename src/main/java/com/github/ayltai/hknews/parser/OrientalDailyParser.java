package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.model.Video;
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OrientalDailyParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrientalDailyParser.class);

    //region Constants

    private static final String BASE_URL = "https://orientaldaily.on.cc";
    private static final String QUOTE    = "\"";
    private static final String SLASH    = "/";
    private static final String CLOSE    = "</div>";

    //endregion

    public OrientalDailyParser(@NonNull final String sourceName, @NonNull final SourceService sourceService, @NonNull final ContentServiceFactory contentServiceFactory) {
        super(sourceName, sourceService, contentServiceFactory);
    }

    @NonNull
    @Override
    public Collection<Item> getItems(@NonNull final String categoryName) {
        final LocalDate now = LocalDate.now();

        return this.sourceService
            .getSources(this.sourceName)
            .stream()
            .filter(source -> source.getCategoryName().equals(categoryName))
            .map(Source::getUrl)
            .map(url -> this.contentServiceFactory.create().getHtml(String.format(url, now.format(DateTimeFormatter.ofPattern("yyyyMMdd")))))
            .map(call -> {
                try {
                    String html = StringUtils.substringBetween(call.execute().body(), "<div id=\"articleList\">", "<!--//articleList-->");

                    if ("國際".equals(categoryName)) html = StringUtils.substringBetween(html, "<h2>要聞</h2>", "<h2>兩岸</h2>");
                    if ("兩岸".equals(categoryName)) html = StringUtils.substringBetween(html, "<h2>兩岸</h2>", OrientalDailyParser.CLOSE);

                    return StringUtils.substringsBetween(html, "<li>", "</li>");
                } catch (final IOException e) {
                    OrientalDailyParser.LOGGER.error(this.getClass().getSimpleName(), e.getMessage(), e);
                }

                return null;
            })
            .filter(Objects::nonNull)
            .flatMap(Stream::of)
            .map(section -> {
                final Item item = new Item();
                item.setTitle(StringUtils.substringBetween(section, "\">", "<"));
                item.setUrl(OrientalDailyParser.BASE_URL + StringUtils.substringBetween(section, "<a href=\"", "\""));
                item.setPublishDate(Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                item.setSourceName(this.sourceName);
                item.setCategoryName(categoryName);

                return item;
            })
            .collect(Collectors.toList());
    }

    @NonNull
    @Override
    public Item updateItem(@NonNull final Item item) throws IOException {
        final String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<div id=\"contentCTN-top\"", "</div><!--//contentCTN-->");
        if (html != null) {
            final String[] descriptions = StringUtils.substringsBetween(html.replace("<h3>", "<p>").replace("</h3>", "</p>"), "<p>", "</p>");
            if (descriptions != null) item.setDescription(String.join("<br><br>", descriptions));

            OrientalDailyParser.processImages(html, item);
            this.processVideos(item);
        }

        return item;
    }

    private static void processImages(@NonNull final String html, @NonNull final Item item) {
        final String[] imageContainers = StringUtils.substringsBetween(html, "<div class=\"photo", OrientalDailyParser.CLOSE);
        if (imageContainers != null) {
            item.getImages().clear();
            item.getImages().addAll(Stream.of(imageContainers)
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, "href=\"", OrientalDailyParser.QUOTE);
                    if (imageUrl == null) return null;

                    return new Image(item, OrientalDailyParser.BASE_URL + imageUrl, StringUtils.substringBetween(imageContainer, "title=\"", OrientalDailyParser.QUOTE));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        }
    }

    private void processVideos(@NonNull final Item item) throws IOException {
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

                    return new Video(item, "https://video-cdn.on.cc/Video/" + shortDate + OrientalDailyParser.SLASH + StringUtils.substringBetween(videoUrl, "?mid=", "&amp;mtype=video") + "_ipad.mp4", "https://tv.on.cc/xml/Thumbnail/" + shortDate + "/bigthumbnail/" + thumbnailUrl);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        }
    }
}
