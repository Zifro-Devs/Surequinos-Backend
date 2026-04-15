package com.surequinos.surequinos_backend.infrastructure.web.controller;

import com.surequinos.surequinos_backend.application.dto.NewsItemDto;
import com.surequinos.surequinos_backend.application.service.NewsItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
@Slf4j
public class NewsItemController {

    private final NewsItemService newsItemService;

    @GetMapping("/active")
    public ResponseEntity<List<NewsItemDto>> getActiveNews() {
        log.info("GET /news/active - Obteniendo noticias activas");
        return ResponseEntity.ok(newsItemService.getActiveNews());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NewsItemDto>> getAllNews() {
        log.info("GET /news - Obteniendo todas las noticias");
        return ResponseEntity.ok(newsItemService.getAllNews());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewsItemDto> createNews(@RequestBody NewsItemDto dto) {
        log.info("POST /news - Creando nueva noticia");
        return ResponseEntity.ok(newsItemService.createNews(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewsItemDto> updateNews(@PathVariable UUID id, @RequestBody NewsItemDto dto) {
        log.info("PUT /news/{} - Actualizando noticia", id);
        return ResponseEntity.ok(newsItemService.updateNews(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteNews(@PathVariable UUID id) {
        log.info("DELETE /news/{} - Eliminando noticia", id);
        newsItemService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }
}
