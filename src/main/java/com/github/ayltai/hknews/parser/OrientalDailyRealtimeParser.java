package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.model.Video;
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OrientalDailyRealtimeParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrientalDailyRealtimeParser.class);

    private static final String SLASH           = "/";
    private static final String JSON_ARTICLE_ID = "articleId";

    public OrientalDailyRealtimeParser(@NonNull final String sourceName, @NonNull final SourceService sourceService, @NonNull final ContentServiceFactory contentServiceFactory) {
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
                    return new JSONArray(call.execute().body());
                } catch (final ProtocolException e) {
                    if (e.getMessage().startsWith("Too many follow-up requests")) OrientalDailyRealtimeParser.LOGGER.info(e.getMessage(), e);
                } catch (final SSLHandshakeException | SocketTimeoutException e) {
                    OrientalDailyRealtimeParser.LOGGER.info(e.getMessage(), e);
                } catch (final SSLException e) {
                    if (e.getMessage().equals("Connection reset")) OrientalDailyRealtimeParser.LOGGER.info(e.getMessage(), e);
                } catch (final IOException e) {
                    OrientalDailyRealtimeParser.LOGGER.error(this.getClass().getSimpleName(), e.getMessage(), e);
                }

                return null;
            })
            .filter(Objects::nonNull)
            .flatMap(objects -> StreamSupport.stream(objects.spliterator(), false))
            .map(object -> (JSONObject)object)
            .map(json -> {
                final Item item = new Item();
                item.setTitle(json.getString("title"));
                item.setUrl("https://hk.on.cc" + json.getString("link"));
                item.setPublishDate(Date.from(LocalDateTime.parse(json.getString("pubDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()).toInstant()));
                item.setSourceName(this.sourceName);
                item.setCategoryName(categoryName);

                return item;
            })
            .collect(Collectors.toList());
    }

    @NonNull
    @Override
    public Item updateItem(@NonNull final Item item) throws IOException {
        final String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body(), "ONCC.Content.data = [", "];");
        if (html != null) {
            final JSONObject json = new JSONObject(html.trim());

            item.setDescription(StreamSupport.stream(json.getJSONArray("contentArray").spliterator(), false)
                .map(object -> (JSONObject)object)
                .map(element -> element.getString("paragraph"))
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining("<br><br>")));

            this.processVideos(OrientalDailyRealtimeParser.processImages(json, item), item);
        }

        return item;
    }

    @NonNull
    private static String processImages(@NonNull final JSONObject json, @NonNull final Item item) {
        item.getImages().clear();
        item.getImages().addAll(StreamSupport.stream(json.getJSONArray("photo").spliterator(), false)
            .map(object -> (JSONObject)object)
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

                return imageUrl == null ? null : new Image(item, "https://hk.on.cc/hk/bkn" + imageUrl, description);
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));

        return json.getString(OrientalDailyRealtimeParser.JSON_ARTICLE_ID);
    }

    private void processVideos(@NonNull final String articleId, @NonNull final Item item) throws IOException {
        final LocalDate date     = item.getPublishDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        final String    fullDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        item.getVideos().clear();
        item.getVideos().addAll(StreamSupport.stream(new JSONArray(this.contentServiceFactory.create().getHtml("https://hk.on.cc/hk/bkn/video/" + fullDate + "/articleVideo_news.js").execute().body()).spliterator(), false)
            .map(object -> (JSONObject)object)
            .filter(json -> articleId.equals(json.getString(OrientalDailyRealtimeParser.JSON_ARTICLE_ID)))
            .map(json -> {
                final String videoUrl = json.getString("vid");
                if (videoUrl == null) return null;

                return new Video(item, "https://video-cdn.on.cc/Video/" + date.format(DateTimeFormatter.ofPattern("yyyyMM")) + OrientalDailyRealtimeParser.SLASH + videoUrl + "_ipad.mp4", "https://hk.on.cc/hk/bkn/cnt/news/" + fullDate + "/photo/" + articleId + "_01p.jpg");
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));
    }
}
