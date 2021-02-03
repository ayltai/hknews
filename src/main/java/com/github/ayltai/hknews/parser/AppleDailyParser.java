package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.model.Video;
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public final class AppleDailyParser extends Parser {
    //region Constants

    private static final String JSON_CONTENT_ELEMENTS = "content_elements";
    private static final String JSON_CONTENT          = "content";
    private static final String JSON_TYPE             = "type";
    private static final String JSON_PROMO_ITEMS      = "promo_items";
    private static final String JSON_BASIC            = "basic";
    private static final String JSON_CAPTION          = "caption";
    private static final String JSON_URL              = "url";
    private static final String LINE_BREAK            = "<br><br>";

    //endregion

    public AppleDailyParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentServiceFactory contentServiceFactory, @NotNull final LambdaLogger logger) {
        super(sourceName, sourceService, contentServiceFactory, logger);
    }

    @NotNull
    @Override
    protected Collection<Item> getItems(@NotNull final Source source) throws IOException {
        final String html = this.contentServiceFactory.create().getHtml(source.getUrl()).execute().body();
        if (html == null) return Collections.emptyList();

        return StreamSupport.stream(new JSONObject(html).getJSONArray(AppleDailyParser.JSON_CONTENT_ELEMENTS).spliterator(), false)
            .map(JSONObject.class::cast)
            .map(json -> {
                final Item item = new Item();
                item.setTitle(json.getJSONObject("headlines").getString(AppleDailyParser.JSON_BASIC));
                item.setUrl("https://hk.appledaily.com" + json.getString("website_url"));
                item.setPublishDate(Date.from(Instant.parse(json.getString("publish_date"))));
                item.setSourceName(source.getSourceName());
                item.setCategoryName(source.getCategoryName());

                AppleDailyParser.processDescriptions(json, item);
                AppleDailyParser.processImages(json, item);
                AppleDailyParser.processVideos(json, item);

                return item;
            })
            .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Item updateItem(@NotNull final Item item) throws IOException {
        return item;
    }

    private static void processDescriptions(@NotNull final JSONObject element, @NotNull final Item item) {
        item.setDescription(StreamSupport.stream(element.getJSONArray(AppleDailyParser.JSON_CONTENT_ELEMENTS).spliterator(), false)
            .map(JSONObject.class::cast)
            .map(json -> {
                final String type = json.optString(AppleDailyParser.JSON_TYPE);

                if ("text".equals(type)) return json.getString(AppleDailyParser.JSON_CONTENT) + AppleDailyParser.LINE_BREAK;
                if ("subhead".equals(type)) return json.getString(AppleDailyParser.JSON_CONTENT).trim() + AppleDailyParser.LINE_BREAK;
                if ("raw_html".equals(type)) return json.getString(AppleDailyParser.JSON_CONTENT);

                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.joining()));
    }

    private static void processImages(@NotNull final JSONObject element, @NotNull final Item item) {
        final JSONObject promoItems = element.optJSONObject(AppleDailyParser.JSON_PROMO_ITEMS);
        if (promoItems != null) {
            final JSONObject image = promoItems.getJSONObject(AppleDailyParser.JSON_BASIC);
            if ("image".equals(image.getString(AppleDailyParser.JSON_TYPE))) {
                final String imageUrl = image.optString(AppleDailyParser.JSON_URL);
                if (imageUrl != null) item.getImages().add(new Image(imageUrl, image.optString(AppleDailyParser.JSON_CAPTION)));
            }

            item.getImages().clear();
            item.getImages().addAll(StreamSupport.stream(element.getJSONArray(AppleDailyParser.JSON_CONTENT_ELEMENTS).spliterator(), false)
                .map(JSONObject.class::cast)
                .filter(json -> "image".equals(json.optString(AppleDailyParser.JSON_TYPE)))
                .map(json -> {
                    final String imageUrl = json.optString(AppleDailyParser.JSON_URL);
                    if (imageUrl == null) return null;

                    return new Image(imageUrl, json.optString(AppleDailyParser.JSON_CAPTION));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        }
    }

    private static void processVideos(@NotNull final JSONObject element, @NotNull final Item item) {
        final JSONObject promoItems = element.optJSONObject(AppleDailyParser.JSON_PROMO_ITEMS);
        if (promoItems != null) {
            final JSONObject video = promoItems.getJSONObject(AppleDailyParser.JSON_BASIC);
            if ("video".equals(video.getString(AppleDailyParser.JSON_TYPE))) {
                item.getVideos().clear();
                item.getVideos().add(new Video(video.getJSONArray("streams").getJSONObject(0).getString(AppleDailyParser.JSON_URL), video.getJSONObject("promo_image").getString(AppleDailyParser.JSON_URL)));
            }
        }
    }
}
