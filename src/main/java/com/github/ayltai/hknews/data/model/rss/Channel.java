package com.github.ayltai.hknews.data.model.rss;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Channel {
    @Getter
    @JsonProperty("item")
    @JacksonXmlElementWrapper(useWrapping = false)
    private Collection<Entry> items;
}
