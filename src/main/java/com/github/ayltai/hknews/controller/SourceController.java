package com.github.ayltai.hknews.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.ayltai.hknews.MainConfiguration;
import com.github.ayltai.hknews.service.SourceService;

@RestController
@RequestMapping(
    path   = "/sources",
    method = {
        RequestMethod.GET,
        RequestMethod.HEAD,
        RequestMethod.OPTIONS,
    })
public class SourceController {
    private final SourceService sourceService;
    private final HttpHeaders   httpHeaders;

    public SourceController(@NonNull final SourceService sourceService, @NonNull final HttpHeaders httpHeaders) {
        this.sourceService = sourceService;
        this.httpHeaders   = httpHeaders;
    }

    @NonNull
    @GetMapping(produces = MainConfiguration.CONTENT_TYPE_JSON)
    public ResponseEntity<List<String>> getSourceNames() {
        return ResponseEntity.ok()
            .headers(this.httpHeaders)
            .body(this.sourceService.getSourceNames());
    }
}
