package com.github.ayltai.hknews.data.model.rss;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Entry {
    @JsonProperty
    private String title;

    @JsonProperty
    private String link;

    @JsonProperty
    private String guid;

    @Getter
    @JsonProperty
    private String description;

    @Getter
    @JsonProperty
    private String pubDate;

    @Getter
    @JsonProperty("enclosure")
    @JacksonXmlElementWrapper(useWrapping = false)
    private Collection<Enclosure> enclosures;

    public String getTitle() {
        return this.title == null ? null : this.title.replace("\uFEFF", "");
    }

    public String getLink() {
        return this.link == null ? this.guid : this.link;
    }
}
