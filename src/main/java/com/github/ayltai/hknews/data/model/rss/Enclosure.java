package com.github.ayltai.hknews.data.model.rss;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Enclosure {
    private static final String TYPE_IMAGE = "image/";
    private static final String TYPE_VIDEO = "video/";

    @Getter
    @JsonProperty
    private String url;

    @Getter
    @JsonProperty
    private long length;

    @Getter
    @JsonProperty
    private String type;

    public boolean isImage() {
        return this.type.startsWith(Enclosure.TYPE_IMAGE);
    }

    public boolean isVideo() {
        return this.type.startsWith(Enclosure.TYPE_VIDEO);
    }
}
