package com.github.ayltai.hknews.controller;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.ayltai.hknews.service.SourceService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
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

    @NonNull
    @Cacheable(
        cacheNames = "sources",
        sync       = true
    )
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<String>> getSourceNames() {
        return ResponseEntity.ok(this.sourceService.getSourceNames());
    }
}
