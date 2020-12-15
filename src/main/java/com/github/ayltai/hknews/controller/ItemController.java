package com.github.ayltai.hknews.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.service.ItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(method = {
    RequestMethod.GET,
    RequestMethod.HEAD,
    RequestMethod.OPTIONS,
})
public class ItemController {
    private final ItemService itemService;

    @Operation(
        summary     = "Get a list of news records",
        description = "Get a list of news records of specific news categories and from specific new publishers",
        parameters  = {
            @Parameter(
                in          = ParameterIn.PATH,
                name        = "sourceNames",
                description = "A comma-separated list of news publisher names",
                schema      = @Schema(
                    type           = "string",
                    implementation = String.class,
                    example        = "蘋果日報,香港電台"
                )
            ),
            @Parameter(
                in          = ParameterIn.PATH,
                name        = "categoryNames",
                description = "A comma-separated list of news categories",
                schema      = @Schema(
                    type           = "string",
                    implementation = String.class,
                    example        = "港聞,國際"
                )
            ),
            @Parameter(
                in          = ParameterIn.PATH,
                name        = "days",
                description = "The number of previous days of news to retrieve",
                schema      = @Schema(
                    type           = "integer",
                    implementation = int.class,
                    example        = "2"
                )
            ),
            @Parameter(
                in          = ParameterIn.QUERY,
                name        = "pageNumber",
                description = "Page offset starting from 0",
                schema      = @Schema(
                    type           = "integer",
                    implementation = int.class,
                    defaultValue   = "0",
                    example        = "1"
                )
            ),
            @Parameter(
                in          = ParameterIn.QUERY,
                name        = "pageSize",
                description = "The number of news records per page",
                schema      = @Schema(
                    type           = "integer",
                    implementation = int.class,
                    defaultValue   = "2147483647",
                    example        = "10"
                )
            ),
        },
        responses   = {
            @ApiResponse(
                responseCode = "200",
                description  = "The requested list of news records was returned successfully",
                content      = {
                    @Content(
                        mediaType = "application/json",
                        schema    = @Schema(implementation = Page.class)
                    ),
                }
            ),
            @ApiResponse(
                responseCode = "400",
                description  = "At least one of the request parameters was missing or invalid"
            ),
            @ApiResponse(
                responseCode = "404",
                description  = "The requested list of news records was not found"
            )
        },
        tags        = {
            "item",
        }
    )
    @NonNull
    @GetMapping(
        path     = "/items/{sourceNames}/{categoryNames}/{days}",
        produces = "application/json"
    )
    public ResponseEntity<Page<Item>> getItems(@Nullable @PathVariable final List<String> sourceNames, @Nullable @PathVariable final List<String> categoryNames, @PathVariable final int days, @RequestParam(defaultValue = "0") final int pageNumber, @RequestParam(defaultValue = "2147483647") final int pageSize) {
        if (sourceNames == null || sourceNames.isEmpty() || categoryNames == null || categoryNames.isEmpty()) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(this.itemService.getItems(sourceNames, categoryNames, days, pageNumber, pageSize));
    }

    @Operation(
        summary     = "Get a news record",
        description = "Get a specific news record by its ID",
        parameters  = {
            @Parameter(
                in          = ParameterIn.PATH,
                name        = "id",
                description = "An unique ID that represents the news record to be retrieved",
                schema      = @Schema(
                    type           = "string",
                    implementation = String.class,
                    format         = "uuid",
                    example        = "3c765a4f-4cf9-46e7-8331-6ed6208c9644"
                ),
                required    = true
            ),
        },
        responses   = {
            @ApiResponse(
                responseCode = "200",
                description  = "The requested news record was returned successfully",
                content      = {
                    @Content(
                        mediaType = "application/json",
                        schema    = @Schema(implementation = Item.class)
                    ),
                }
            ),
            @ApiResponse(
                responseCode = "400",
                description  = "The \"id\" parameter was not specified in the request"
            ),
            @ApiResponse(
                responseCode = "404",
                description  = "The requested news record was not found"
            ),
        },
        tags        = {
            "item",
        }
    )
    @NonNull
    @GetMapping(
        path     = "/item/{id}",
        produces = "application/json"
    )
    public ResponseEntity<Item> getItem(@Nullable @PathVariable final Integer id) {
        if (id == null) return ResponseEntity.badRequest().build();

        final Optional<Item> item = this.itemService.getItem(id);

        return item.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(item.get());
    }
}
