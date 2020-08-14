package com.github.ayltai.hknews.controller;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.ayltai.hknews.MainConfiguration;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.service.ItemService;

@RestController
@RequestMapping(method = {
    RequestMethod.GET,
    RequestMethod.HEAD,
    RequestMethod.OPTIONS,
})
public class ItemController {
    private final ItemService itemService;
    private final HttpHeaders httpHeaders;

    public ItemController(@NonNull final ItemService itemService, @NonNull final HttpHeaders httpHeaders) {
        this.itemService = itemService;
        this.httpHeaders = httpHeaders;
    }

    @NonNull
    @GetMapping(
        path     = "/item/{id}",
        produces = MainConfiguration.CONTENT_TYPE_JSON
    )
    public ResponseEntity<Item> getItem(@Nullable @PathVariable final String id) {
        if (id == null || id.isEmpty()) return ResponseEntity.badRequest().build();

        return this.itemService.getItem(id).map(value -> ResponseEntity.ok().headers(this.httpHeaders).body(value)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @NonNull
    @Cacheable(
        cacheNames = "items",
        sync       = true
    )
    @GetMapping(
        path     = "/items/{sourceNames}/{categoryNames}/{days}",
        produces = MainConfiguration.CONTENT_TYPE_JSON
    )
    public ResponseEntity<Page<Item>> getItems(@Nullable @PathVariable final List<String> sourceNames, @Nullable @PathVariable final List<String> categoryNames, @PathVariable final int days, @RequestParam(name = "page", defaultValue = "0") final int page, @RequestParam(name = "size", defaultValue = MainConfiguration.DEFAULT_PAGE_SIZE) final int size) {
        if (sourceNames == null || sourceNames.isEmpty() || categoryNames == null || categoryNames.isEmpty()) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok()
            .headers(this.httpHeaders)
            .body(this.itemService.getItems(sourceNames, categoryNames, days, page, size));
    }
}
