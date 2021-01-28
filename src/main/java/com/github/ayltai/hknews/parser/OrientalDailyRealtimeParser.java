package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.model.Video;
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public final class OrientalDailyRealtimeParser extends Parser {
    private static final String SLASH           = "/";
    private static final String JSON_ARTICLE_ID = "articleId";

    public OrientalDailyRealtimeParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentServiceFactory contentServiceFactory, @NotNull final LambdaLogger logger) {
        super(sourceName, sourceService, contentServiceFactory, logger);
    }

    @NotNull
    @Override
    protected Collection<Item> getItems(@NotNull final Source source) throws ProtocolException, SSLHandshakeException, SocketTimeoutException, SSLException, IOException {
        final LocalDate now = LocalDate.now();

        final String html = this.contentServiceFactory.create().getHtml(String.format(source.getUrl(), now.format(DateTimeFormatter.ofPattern("yyyyMMdd")))).execute().body();
        if (html == null) return Collections.emptyList();

        return StreamSupport.stream(new JSONArray(html).spliterator(), false)
            .map(JSONObject.class::cast)
            .map(json -> {
                final Item item = new Item();
                item.setTitle(json.getString("title"));
                item.setUrl("https://hk.on.cc" + json.getString("link"));
                item.setPublishDate(Date.from(LocalDateTime.parse(json.getString("pubDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()).toInstant()));
                item.setSourceName(source.getSourceName());
                item.setCategoryName(source.getCategoryName());

                return item;
            })
            .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Item updateItem(@NotNull final Item item) throws IOException {
        final String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body(), "ONCC.Content.data = [", "];");
        if (html != null) {
            final JSONObject json = new JSONObject(html.trim());

            item.setDescription(StreamSupport.stream(json.getJSONArray("contentArray").spliterator(), false)
                .map(JSONObject.class::cast)
                .map(element -> element.getString("paragraph"))
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining("<br><br>")));

            this.processVideos(OrientalDailyRealtimeParser.processImages(json, item), item);
        }

        return item;
    }

    @NotNull
    private static String processImages(@NotNull final JSONObject json, @NotNull final Item item) {
        item.getImages().clear();
        item.getImages().addAll(StreamSupport.stream(json.getJSONArray("photo").spliterator(), false)
            .map(JSONObject.class::cast)
            .map(element -> {
                String imageUrl    = null;
                String description = null;

                final Iterator<String> keys = element.keys();
                while (keys.hasNext()) {
                    final String key = keys.next();
                    if (key.startsWith("original")) {
                        imageUrl = element.getString(key);
                    } else if (key.startsWith("caption")) {
                        description = element.getString(key);
                    }
                }

                return imageUrl == null ? null : new Image("https://hk.on.cc/hk/bkn" + imageUrl, description);
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));

        return json.getString(OrientalDailyRealtimeParser.JSON_ARTICLE_ID);
    }

    private void processVideos(@NotNull final String articleId, @NotNull final Item item) throws IOException {
        final LocalDate date     = item.getPublishDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        final String    fullDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        item.getVideos().clear();

        final String videos = this.contentServiceFactory.create().getHtml("https://hk.on.cc/hk/bkn/video/" + fullDate + "/articleVideo_news.js").execute().body();
        if (videos != null) item.getVideos().addAll(StreamSupport.stream(new JSONArray(videos).spliterator(), false)
            .map(JSONObject.class::cast)
            .filter(json -> articleId.equals(json.getString(OrientalDailyRealtimeParser.JSON_ARTICLE_ID)))
            .map(json -> {
                final String videoUrl = json.getString("vid");
                if (videoUrl == null) return null;

                return new Video("https://video-cdn.on.cc/Video/" + date.format(DateTimeFormatter.ofPattern("yyyyMM")) + OrientalDailyRealtimeParser.SLASH + videoUrl + "_ipad.mp4", "https://hk.on.cc/hk/bkn/cnt/news/" + fullDate + "/photo/" + articleId + "_01p.jpg");
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));
    }
}
