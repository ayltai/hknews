package com.github.ayltai.hknews.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.ayltai.hknews.MainConfiguration;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.service.SourceService;

@RestController
@RequestMapping(
    path   = "/sources",
    method = {
        RequestMethod.GET,
        RequestMethod.HEAD,
        RequestMethod.OPTIONS,
    }
)
public class SourceController {
    private final SourceService sourceService;
    private final HttpHeaders   httpHeaders;

    public SourceController(@NonNull final SourceService sourceService, @NonNull final HttpHeaders httpHeaders) {
        this.sourceService = sourceService;
        this.httpHeaders   = httpHeaders;
    }

    @NonNull
    @Cacheable(
        cacheNames = "sources",
        sync       = true
    )
    @GetMapping(produces = MainConfiguration.CONTENT_TYPE_JSON)
    public ResponseEntity<Page<Source>> getSources(@RequestParam(name = "page", defaultValue = "0") final int page, @RequestParam(name = "size", defaultValue = MainConfiguration.DEFAULT_PAGE_SIZE) final int size) {
        return ResponseEntity.ok()
            .headers(this.httpHeaders)
            .body(this.sourceService.getSources(page, size));
    }
}
