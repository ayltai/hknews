package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Category;
import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Video;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ContentServiceFactory;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AppleDailyParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppleDailyParser.class);

    private static final String JSON_CONTENT_ELEMENTS = "content_elements";
    private static final String JSON_CONTENT          = "content";
    private static final String JSON_TYPE             = "type";
    private static final String JSON_PROMO_ITEMS      = "promo_items";
    private static final String JSON_BASIC            = "basic";
    private static final String JSON_CAPTION          = "caption";
    private static final String JSON_URL              = "url";
    private static final String LINE_BREAK            = "<br><br>";

    public AppleDailyParser(@NonNull final String sourceName, @NonNull final SourceRepository sourceRepository, @NonNull final ContentServiceFactory contentServiceFactory) {
        super(sourceName, sourceRepository, contentServiceFactory);
    }

    @NonNull
    @Override
    public Collection<Item> getItems(@NonNull final String categoryName) {
        return this.sourceRepository
            .findByName(this.sourceName)
            .getCategories()
            .stream()
            .filter(category -> category.getName().equals(categoryName))
            .map(Category::getUrls)
            .flatMap(List::stream)
            .map(url -> {
                try {
                    return new JSONObject(this.contentServiceFactory.create().getHtml(url).execute().body()).getJSONArray(AppleDailyParser.JSON_CONTENT_ELEMENTS);
                } catch (final IOException e) {
                    AppleDailyParser.LOGGER.error(this.getClass().getSimpleName(), e.getMessage(), e);
                }

                return Collections.emptyList();
            })
            .flatMap(array -> StreamSupport.stream(array.spliterator(), false))
            .map(object -> (JSONObject)object)
            .map(json -> {
                final Item item = new Item();
                item.setTitle(json.getJSONObject("headlines").getString(AppleDailyParser.JSON_BASIC));
                item.setUrl("https://hk.appledaily.com" + json.getString("website_url"));
                item.setPublishDate(Date.from(Instant.parse(json.getString("publish_date"))));
                item.setSourceName(this.sourceName);
                item.setCategoryName(categoryName);

                AppleDailyParser.processDescriptions(json, item);
                AppleDailyParser.processImages(json, item);
                AppleDailyParser.processVideos(json, item);

                return item;
            })
            .collect(Collectors.toList());
    }

    @NonNull
    @Override
    public Item updateItem(@NonNull final Item item) throws IOException {
        return item;
    }

    private static void processDescriptions(@NonNull final JSONObject element, @NonNull final Item item) {
        item.setDescription(StreamSupport.stream(element.getJSONArray(AppleDailyParser.JSON_CONTENT_ELEMENTS).spliterator(), false)
            .map(object -> (JSONObject)object)
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

    private static void processImages(@NonNull final JSONObject element, @NonNull final Item item) {
        final JSONObject promoItems = element.optJSONObject(AppleDailyParser.JSON_PROMO_ITEMS);
        if (promoItems != null) {
            final JSONObject image = promoItems.getJSONObject(AppleDailyParser.JSON_BASIC);
            if ("image".equals(image.getString(AppleDailyParser.JSON_TYPE))) {
                final String imageUrl = image.optString(AppleDailyParser.JSON_URL);
                if (imageUrl != null) item.getImages().add(new Image(imageUrl, image.optString(AppleDailyParser.JSON_CAPTION)));
            }

            item.getImages().addAll(StreamSupport.stream(element.getJSONArray(AppleDailyParser.JSON_CONTENT_ELEMENTS).spliterator(), false)
                .map(object -> (JSONObject)object)
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

    private static void processVideos(@NonNull final JSONObject element, @NonNull final Item item) {
        final JSONObject promoItems = element.optJSONObject(AppleDailyParser.JSON_PROMO_ITEMS);
        if (promoItems != null) {
            final JSONObject video = promoItems.getJSONObject(AppleDailyParser.JSON_BASIC);
            if ("video".equals(video.getString(AppleDailyParser.JSON_TYPE))) item.getVideos().add(new Video(video.getJSONArray("streams").getJSONObject(0).getString(AppleDailyParser.JSON_URL), video.getJSONObject("promo_image").getString(AppleDailyParser.JSON_URL)));
        }
    }
}
