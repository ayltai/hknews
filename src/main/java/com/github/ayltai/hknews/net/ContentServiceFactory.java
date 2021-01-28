package com.github.ayltai.hknews.net;

import org.jetbrains.annotations.NotNull;

public interface ContentServiceFactory {
    @NotNull
    ContentService create();
}
