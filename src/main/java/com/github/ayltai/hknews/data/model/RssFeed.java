package com.github.ayltai.hknews.data.model;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import lombok.Getter;

@Root(name = "rss", strict = false)
public final class RssFeed {
    @Getter
    @Element(required = false)
    @Path("channel")
    private String title;

    @Getter
    @Element(required = false)
    @Path("channel")
    private String description;

    @Getter
    @Element(required = false)
    @Path("channel")
    private String copyright;

    @Getter
    @ElementList(
        name     = "item",
        required = false,
        inline   = true)
    @Path("channel")
    private List<RssItem> items;
}
