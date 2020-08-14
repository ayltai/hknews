package com.github.ayltai.hknews.data.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import lombok.Getter;

@Root(name = "enclosure", strict = false)
public final class RssEnclosure {
    private static final String TYPE_IMAGE = "image/";
    private static final String TYPE_VIDEO = "video/";

    @Getter
    @Attribute
    private String url;

    @Getter
    @Attribute(required = false)
    private long length;

    @Getter
    @Attribute(required = false)
    private String type;

    public boolean isImage() {
        return this.type.startsWith(RssEnclosure.TYPE_IMAGE);
    }

    public boolean isVideo() {
        return this.type.startsWith(RssEnclosure.TYPE_VIDEO);
    }
}
