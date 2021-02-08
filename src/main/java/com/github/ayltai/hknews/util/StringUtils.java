package com.github.ayltai.hknews.util;

import java.util.ArrayList;
import java.util.List;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class StringUtils {
    private static final int INDEX_NOT_FOUND = -1;

    @Nullable
    public String substringBetween(@NotNull final String str, @NotNull final String open, @NotNull final String close) {
        final int start = str.indexOf(open);
        if (start != StringUtils.INDEX_NOT_FOUND) {
            final int end = str.indexOf(close, start + open.length());
            if (end != StringUtils.INDEX_NOT_FOUND) return str.substring(start + open.length(), end);
        }

        return null;
    }

    @Nullable
    public String[] substringsBetween(@NotNull final String str, @NotNull final String open, @NotNull final String close) {
        if (str.length() == 0) return new String[0];

        final List<String> list = new ArrayList<>();

        int position = 0;

        while (position < str.length() - close.length()) {
            int start = str.indexOf(open, position);
            if (start == StringUtils.INDEX_NOT_FOUND) break;

            start += open.length();

            final int end = str.indexOf(close, start);
            if (end == StringUtils.INDEX_NOT_FOUND) break;

            list.add(str.substring(start, end));
            position = end + close.length();
        }

        return list.isEmpty() ? null : list.toArray(new String[0]);
    }

    public boolean isNotEmpty(@Nullable final String str) {
        return str != null && !str.isEmpty();
    }
}
