package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
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
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public final class WenWeiPoParser extends Parser {
    //region Constants

    private static final String CLOSE_QUOTE     = "\"";
    private static final String CLOSE_PARAGRAPH = "</p>";
    private static final String LINE_BREAKS     = "<br><br>";

    //endregion

    public WenWeiPoParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentServiceFactory contentServiceFactory, @NotNull final LambdaLogger logger) {
        super(sourceName, sourceService, contentServiceFactory, logger);
    }

    @SuppressWarnings("java:S3776")
    @NotNull
    @Override
    protected Collection<Item> getItems(@NotNull final Source source) throws IOException {
        final String[] sections = StringUtils.substringsBetween(this.contentServiceFactory.create().getHtml(source.getUrl()).execute().body(), "<div class=\"content-art-box\">", "</article>");
        if (sections == null) return Collections.emptyList();

        final LocalDateTime now = LocalDateTime.now();

        return Stream.of(sections)
            .filter(Objects::nonNull)
            .map(section -> {
                final String url = StringUtils.substringBetween(section, "<a href=\"", WenWeiPoParser.CLOSE_QUOTE);
                if (url == null) return null;

                final String date = StringUtils.substringBetween(section, "<p class=\"date\">[ ", " ]</p>");
                if (date == null) return null;

                final Item item = new Item();
                item.setTitle(StringUtils.substringBetween(section, "target=\"_blank\">", "</a>").trim());
                item.setDescription(StringUtils.substringBetween(section, "<p class=\"txt\">", WenWeiPoParser.CLOSE_PARAGRAPH));
                item.setUrl(url);
                item.setSourceName(source.getSourceName());
                item.setCategoryName(source.getCategoryName());

                final String[] tokens = date.split("日 ");
                final String[] times  = tokens[1].split(":");

                final int month = now.getMonthValue() - (now.getDayOfMonth() == Integer.parseInt(tokens[0]) ? 0 : 1);

                item.setPublishDate(Date.from(now.withMonth(month == 0 ? Calendar.DECEMBER + 1 : month)
                    .withDayOfMonth(Integer.parseInt(tokens[0]))
                    .withHour(Integer.parseInt(times[0]))
                    .withMinute(Integer.parseInt(times[1]))
                    .atZone(ZoneId.systemDefault())
                    .toInstant()));

                return item;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Item updateItem(@NotNull final Item item) throws IOException {
        final String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<!-- Content start -->", "!-- Content end -->");
        if (html != null) {
            String[] descriptions = StringUtils.substringsBetween(html, "<p >", WenWeiPoParser.CLOSE_PARAGRAPH);
            if (descriptions != null) item.setDescription(String.join(WenWeiPoParser.LINE_BREAKS, descriptions));

            descriptions = StringUtils.substringsBetween(html, "<p>", WenWeiPoParser.CLOSE_PARAGRAPH);
            if (descriptions != null) item.setDescription((item.getDescription() == null ? "" : item.getDescription()) + String.join(WenWeiPoParser.LINE_BREAKS, descriptions));

            WenWeiPoParser.processImages(html, item);
        }

        return item;
    }

    private static void processImages(@NotNull final String html, @NotNull final Item item) {
        final String[] imageContainers = StringUtils.substringsBetween(html, "<img ", ">");
        if (imageContainers != null) {
            item.getImages().clear();
            item.getImages().addAll(Stream.of(imageContainers)
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, "src=\"", WenWeiPoParser.CLOSE_QUOTE);
                    if (imageUrl == null) return null;

                    return new Image(imageUrl, StringUtils.substringBetween(imageContainer, "alt=\"", WenWeiPoParser.CLOSE_QUOTE));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        }
    }
}
