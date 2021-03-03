package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.Configuration;
import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.net.ContentService;
import com.github.ayltai.hknews.service.SourceService;
import com.github.ayltai.hknews.util.StringUtils;

import org.jetbrains.annotations.NotNull;

public final class SingPaoParser extends Parser {
    //region Constants

    private static final String BASE_URI  = "https://www.singpao.com.hk/";
    private static final String QUOTE     = "'";
    private static final String FONT      = "</font>";
    private static final String PARAGRAPH = "</p>";

    //endregion

    public SingPaoParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentService contentService, @NotNull final LambdaLogger logger) {
        super(sourceName, sourceService, contentService, logger);
    }

    @NotNull
    @Override
    protected Collection<Item> getItems(@NotNull final Source source) throws IOException {
        final String[] sections = StringUtils.substringsBetween(this.contentService.getHtml(source.getUrl()), "<tr valign='top'><td width='220'>", "</td></tr>");
        if (sections == null) return Collections.emptyList();

        return Stream.of(sections)
            .filter(Objects::nonNull)
            .map(section -> {
                final String url = StringUtils.substringBetween(section, "<td><a href='", SingPaoParser.QUOTE);
                if (url == null) return null;

                final String date = StringUtils.substringBetween(section, "<font class='list_date'>", "<br>");
                if (date == null) return null;

                final Item item = new Item();
                item.setTitle(StringUtils.substringBetween(section, "class='list_title'>", "</a>").trim());
                item.setDescription(StringUtils.substringBetween(section, "<br><br>\n", SingPaoParser.FONT));
                item.setUrl(SingPaoParser.BASE_URI + url);
                item.setPublishDate(Date.from(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay(ZoneId.of(Configuration.DEFAULT.getTimeZone())).toInstant()));
                item.setSourceName(source.getSourceName());
                item.setCategoryName(source.getCategoryName());

                return item;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Item updateItem(@NotNull final Item item) throws IOException {
        final String html = StringUtils.substringBetween(this.contentService.getHtml(item.getUrl()), "<td class='news_title'>", "您可能有興趣:");
        if (html != null) {
            String[] descriptions = StringUtils.substringsBetween(html, "<p class=\"內文\">", SingPaoParser.PARAGRAPH);
            if (descriptions == null) descriptions = StringUtils.substringsBetween(html, "<p>", SingPaoParser.PARAGRAPH);
            if (descriptions != null) item.setDescription(String.join("<br><br>", descriptions));

            SingPaoParser.processImages(html, item);
        }

        return item;
    }

    private static void processImages(@NotNull final String html, @NotNull final Item item) {
        final String[] imageUrls = StringUtils.substringsBetween(html, "target='_blank'><img src='", SingPaoParser.QUOTE);
        final String[] imageDescriptions = StringUtils.substringsBetween(html, "<font size='4'>", SingPaoParser.FONT);

        if (imageUrls != null && imageUrls.length > 0) {
            for (int i = 0; i < imageUrls.length; i++) item.getImages().add(new Image(SingPaoParser.BASE_URI + imageUrls[i], imageDescriptions == null ? null : imageDescriptions.length > i ? imageDescriptions[i] : null));
        }

        final List<Image> images = item.getImages()
            .stream()
            .distinct()
            .collect(Collectors.toList());

        item.getImages().clear();
        item.getImages().addAll(images);
    }
}
