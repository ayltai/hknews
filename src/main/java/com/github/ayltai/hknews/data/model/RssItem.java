package com.github.ayltai.hknews.data.model;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import lombok.Getter;

@Root(
    name   = "item",
    strict = false
)
public final class RssItem {
    @Path("title")
    @Text(data = true)
    private String title;

    @Path("link")
    @Text(
        required = false,
        data     = true
    )
    private String link;

    @Path("guid")
    @Text(
        required = false,
        data     = true
    )
    private String guid;

    @Getter
    @Path("description")
    @Text(
        required = false,
        data     = true
    )
    private String description;

    @Getter
    @Path("pubDate")
    @Text
    private String pubDate;

    @Getter
    @ElementList(
        name     = "enclosure",
        required = false,
        inline   = true,
        type     = RssEnclosure.class
    )
    private List<RssEnclosure> enclosures;

    public String getTitle() {
        return this.title == null ? null : this.title.replace("\uFEFF", "");
    }

    public String getLink() {
        return this.link == null ? this.guid : this.link;
    }
}
