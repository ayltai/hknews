package com.github.ayltai.hknews.data.model.rss;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName("rss")
public final class Root {
    @Getter
    @JsonProperty
    private Channel channel;
}
