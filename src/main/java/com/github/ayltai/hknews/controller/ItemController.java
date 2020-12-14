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

    @NonNull
    @GetMapping(
        path     = "/items/{sourceNames}/{categoryNames}/{days}",
        produces = "application/json"
    )
    public ResponseEntity<Page<Item>> getItems(@Nullable @PathVariable final List<String> sourceNames, @Nullable @PathVariable final List<String> categoryNames, @PathVariable final int days, @RequestParam(defaultValue = "0") final int pageNumber, @RequestParam(defaultValue = "2147483647") final int pageSize) {
        if (sourceNames == null || sourceNames.isEmpty() || categoryNames == null || categoryNames.isEmpty()) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(this.itemService.getItems(sourceNames, categoryNames, days, pageNumber, pageSize));
    }

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
