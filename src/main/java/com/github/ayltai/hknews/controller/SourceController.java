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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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

    @Operation(
        summary     = "Get a list of news publisher names",
        description = "Get a list of news publisher names supported",
        responses   = {
            @ApiResponse(
                responseCode = "200",
                description  = "The requested list of news publisher names was returned successfully",
                content      = {
                    @Content(
                        mediaType = "application/json",
                        schema    = @Schema(
                            type           = "array",
                            implementation = String[].class
                        ),
                        examples  = {
                            @ExampleObject("[\"蘋果日報\",\"香港電台\"]"),
                        }
                    ),
                }
            ),
        },
        tags        = {
            "source",
        }
    )
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
