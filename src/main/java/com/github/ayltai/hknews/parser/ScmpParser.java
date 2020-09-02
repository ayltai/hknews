package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Video;
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public final class ScmpParser extends RssParser {
    //region Constants

    private static final String JSON_ID       = "id";
    private static final String JSON_JSON     = "json";
    private static final String JSON_TYPE     = "type";
    private static final String JSON_DATA     = "data";
    private static final String JSON_TEXT     = "text";
    private static final String JSON_CHILDREN = "children";
    private static final String LINE_BREAK    = "<br><br>";
    private static final String YOUTUBE_URL   = "https://www.youtube.com/embed/";

    //endregion

    public ScmpParser(@NonNull final String sourceName, @NonNull final SourceService sourceService, @NonNull final ContentServiceFactory contentServiceFactory) {
        super(sourceName, sourceService, contentServiceFactory);
    }

    @NonNull
    @Override
    public Item updateItem(@NonNull final Item item) throws IOException {
        final String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<script>window.__APOLLO_STATE__=", "</script>");
        if (html != null) {
            final JSONObject json = new JSONObject(html).getJSONObject("contentService");

            final JSONObject query = ScmpParser.find(json.getJSONObject("ROOT_QUERY"), "content");
            if (query == null) return item;

            final JSONObject content = json.getJSONObject(query.getString(ScmpParser.JSON_ID));
            if (content == null) return item;

            item.setTitle(content.getString("headline"));

            final JSONObject body = ScmpParser.find(content, "body");
            if (body == null) return item;

            final String subHeadline = ScmpParser.extractSubHeadline(content.getJSONObject("subHeadline").getJSONArray(ScmpParser.JSON_JSON));
            final String description = ScmpParser.extractBody(body.getJSONArray(ScmpParser.JSON_JSON));

            item.setDescription(subHeadline + ScmpParser.LINE_BREAK + description);
            item.getImages().clear();
            item.getImages().addAll(ScmpParser.extractImages(content.getJSONArray("images"), json, item));

            final JSONArray videos = ScmpParser.find(content, "articleVideos");
            if (videos != null) {
                item.getVideos().clear();
                item.getVideos().addAll(ScmpParser.extractVideos(videos, json, item));
            }
        }

        return item;
    }

    @Nullable
    private static <T> T find(@NonNull final JSONObject json, @NonNull final String prefix) {
        final Iterator<String> iterator = json.keys();

        while (iterator.hasNext()) {
            final String key = iterator.next();
            if (key.startsWith(prefix) && key.endsWith(")")) return (T)json.get(key);
        }

        return null;
    }

    @NonNull
    private static String extractSubHeadline(@NonNull final JSONArray elements) {
        return StreamSupport.stream(elements.spliterator(), false)
            .map(element -> (JSONObject)element)
            .map(json -> {
                final String type = json.getString(ScmpParser.JSON_TYPE);

                if (ScmpParser.JSON_TEXT.equals(type)) return json.getString(ScmpParser.JSON_DATA);
                if (json.has(ScmpParser.JSON_CHILDREN)) return ScmpParser.extractSubHeadline(json.getJSONArray(ScmpParser.JSON_CHILDREN));

                return null;
            })
            .filter(Objects::nonNull)
            .filter(text -> !text.trim().isEmpty())
            .collect(Collectors.joining(ScmpParser.LINE_BREAK));
    }

    @NonNull
    private static String extractBody(@NonNull final JSONArray elements) {
        return StreamSupport.stream(elements.spliterator(), false)
            .map(element -> (JSONObject)element)
            .map(json -> {
                final String type = json.getString(ScmpParser.JSON_TYPE);

                if (ScmpParser.JSON_TEXT.equals(type)) return json.getString(ScmpParser.JSON_DATA);
                if ("p".equals(type) || "em".equals(type) || "string".equals(type)) return ScmpParser.extractBody(json.getJSONArray(ScmpParser.JSON_CHILDREN));

                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.joining(ScmpParser.LINE_BREAK));
    }

    @NonNull
    private static List<Image> extractImages(@NonNull final JSONArray elements, @NonNull final JSONObject parent, @NonNull final Item item) {
        return StreamSupport.stream(elements.spliterator(), false)
            .map(element -> (JSONObject)element)
            .map(json -> json.getString(ScmpParser.JSON_ID))
            .map(parent::getJSONObject)
            .map(img -> {
                final String imageUrl = img.optString("url");
                if (imageUrl == null) return null;

                return new Image(null, item, imageUrl, img.optString("title"));
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @NonNull
    private static List<Video> extractVideos(@NonNull final JSONArray elements, @NonNull final JSONObject parent, @NonNull final Item item) {
        return StreamSupport.stream(elements.spliterator(), false)
            .map(element -> (JSONObject)element)
            .map(json -> json.getString(ScmpParser.JSON_ID))
            .map(parent::getJSONObject)
            .map(video -> video.getString("videoId"))
            .map(videoId -> new Video(null, item, ScmpParser.YOUTUBE_URL + videoId, ScmpParser.YOUTUBE_URL + videoId + "/hqdefault.jpg"))
            .collect(Collectors.toList());
    }
}
