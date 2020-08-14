package com.github.ayltai.hknews.net;

import org.springframework.lang.NonNull;

public interface ContentServiceFactory {
    @NonNull
    ContentService create();
}
