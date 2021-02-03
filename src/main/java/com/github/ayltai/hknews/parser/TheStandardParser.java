package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Pattern;
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

public final class TheStandardParser extends Parser {
    //region Constants

    private static final String CLOSE_QUOTE     = "\"";
    private static final String OPEN_HREF       = "<a href=\"";
    private static final String OPEN_PARAGRAPH  = "<p>";
    private static final String CLOSE_PARAGRAPH = "</p>";

    private static final String FORMAT_LONG  = "d MMM yyyy h:mm a";
    private static final String FORMAT_SHORT = "d MMM yyyy";

    //endregion

    public TheStandardParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentServiceFactory contentServiceFactory, @NotNull final LambdaLogger logger) {
        super(sourceName, sourceService, contentServiceFactory, logger);
    }

    @NotNull
    @Override
    protected Collection<Item> getItems(@NotNull final Source source) throws IOException {
        final String[] tokens   = source.getUrl().split(Pattern.quote("?"));
        final String[] sections = StringUtils.substringsBetween(this.contentServiceFactory.create().postHtml(tokens[0], Integer.parseInt(tokens[1].split("=")[1]), 1).execute().body(), "<li class='caption'>", "</li>");

        if (sections == null) return Collections.emptyList();

        return Stream.of(sections)
            .filter(Objects::nonNull)
            .map(section -> {
                final String url = StringUtils.substringBetween(section, TheStandardParser.OPEN_HREF, TheStandardParser.CLOSE_QUOTE);
                if (url == null) return null;

                final String date = StringUtils.substringBetween(section, "<span>", "</span>");
                if (date == null) return null;

                final Item item = new Item();
                item.setTitle(StringUtils.substringBetween(StringUtils.substringBetween(section, "<h1>", "</h1>"), "\">", "</a>").trim());
                item.setDescription(StringUtils.substringBetween(section, TheStandardParser.OPEN_PARAGRAPH, TheStandardParser.CLOSE_PARAGRAPH));
                item.setUrl(url);
                item.setPublishDate(Date.from(date.length() > TheStandardParser.FORMAT_SHORT.length() + 1 ? LocalDateTime.parse(date, new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(TheStandardParser.FORMAT_LONG).toFormatter()).atZone(ZoneId.systemDefault()).toInstant() : LocalDate.parse(date, DateTimeFormatter.ofPattern(TheStandardParser.FORMAT_SHORT)).atStartOfDay(ZoneId.systemDefault()).toInstant()));
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
        final String html = StringUtils.substringBetween(this.contentServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<div class=\"content\">", "<div class=\"related\">");
        if (html != null) {
            final String[] descriptions = StringUtils.substringsBetween(html, TheStandardParser.OPEN_PARAGRAPH, TheStandardParser.CLOSE_PARAGRAPH);
            if (descriptions != null) item.setDescription(String.join("<br><br>", descriptions));

            TheStandardParser.processImages(html, item);
        }

        return item;
    }

    private static void processImages(@NotNull final String html, @NotNull final Item item) {
        final String[] imageContainers = StringUtils.substringsBetween(html, "<figure>", "</figure>");
        if (imageContainers != null) {
            item.getImages().clear();
            item.getImages().addAll(Stream.of(imageContainers)
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, TheStandardParser.OPEN_HREF, TheStandardParser.CLOSE_QUOTE);
                    if (imageUrl == null) return null;

                    return new Image(imageUrl, StringUtils.substringBetween(imageContainer, "<i>", "</i>"));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        }
    }
}
