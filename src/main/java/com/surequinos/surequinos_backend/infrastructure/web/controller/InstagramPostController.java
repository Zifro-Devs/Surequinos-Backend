package com.surequinos.surequinos_backend.infrastructure.web.controller;

import com.surequinos.surequinos_backend.application.dto.InstagramPostDto;
import com.surequinos.surequinos_backend.application.service.InstagramPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/instagram")
@RequiredArgsConstructor
@Slf4j
public class InstagramPostController {

    private final InstagramPostService instagramPostService;

    @GetMapping("/active")
    public ResponseEntity<List<InstagramPostDto>> getActivePosts() {
        log.info("GET /instagram/active - Obteniendo posts activos");
        return ResponseEntity.ok(instagramPostService.getActivePosts());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InstagramPostDto>> getAllPosts() {
        log.info("GET /instagram - Obteniendo todos los posts");
        return ResponseEntity.ok(instagramPostService.getAllPosts());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InstagramPostDto> createPost(@RequestBody InstagramPostDto dto) {
        log.info("POST /instagram - Creando nuevo post");
        return ResponseEntity.ok(instagramPostService.createPost(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InstagramPostDto> updatePost(@PathVariable UUID id, @RequestBody InstagramPostDto dto) {
        log.info("PUT /instagram/{} - Actualizando post", id);
        return ResponseEntity.ok(instagramPostService.updatePost(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        log.info("DELETE /instagram/{} - Eliminando post", id);
        instagramPostService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
