package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.net.ContentService;
import com.github.ayltai.hknews.service.SourceService;
import com.github.ayltai.hknews.util.StringUtils;

import org.jetbrains.annotations.NotNull;

public final class HkejParser extends BaseHkejParser {
    public HkejParser(@NotNull final String sourceName, @NotNull final SourceService sourceService, @NotNull final ContentService contentService, @NotNull final LambdaLogger logger) {
        super(sourceName, sourceService, contentService, logger);
    }

    @NotNull
    @Override
    protected Collection<Item> getItems(@NotNull final Source source) throws IOException {
        final LocalDate now = LocalDate.now();

        final String[] sections = StringUtils.substringsBetween(this.contentService.getHtml(source.getUrl()), "<h2>", "</div>");
        if (sections == null) return Collections.emptyList();

        return Stream.of(sections)
            .filter(Objects::nonNull)
            .map(section -> {
                final String url = StringUtils.substringBetween(section, "<a href=\"", BaseHkejParser.QUOTE);
                if (url == null) return null;

                final Item item = new Item();
                item.setTitle(StringUtils.substringBetween(section, "\">", "<br>"));
                item.setUrl("https://www1.hkej.com" + url);
                item.setPublishDate(Date.from(now.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
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
        final String html        = this.contentService.getHtml(item.getUrl());
        final String description = StringUtils.substringBetween(this.contentService.getHtml(item.getUrl()), "<p></p>", "<p>（節錄）</p>");

        if (description != null) item.setDescription(description.trim());

        BaseHkejParser.processImages(html, item);

        return item;
    }
}
