package com.github.ayltai.hknews.net;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.github.ayltai.hknews.data.model.rss.Root;

import org.jetbrains.annotations.NotNull;

public class ContentService {
    @NotNull
    public Root getRss(@NotNull final String url) throws IOException {
        return RestClient.get(url, Root.class);
    }

    @NotNull
    public String getHtml(@NotNull final String url) throws IOException {
        return RestClient.get(url);
    }

    @NotNull
    public String postHtml(@NotNull final String url, final int sectionId, final int page) throws IOException {
        final Map<String, String> fields = new HashMap<>();
        fields.put("sid", String.valueOf(sectionId));
        fields.put("p", String.valueOf(page));

        return RestClient.post(url, fields);
    }
}
