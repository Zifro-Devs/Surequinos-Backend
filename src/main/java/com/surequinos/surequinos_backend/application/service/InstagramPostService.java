package com.surequinos.surequinos_backend.application.service;

import com.surequinos.surequinos_backend.application.dto.InstagramPostDto;
import com.surequinos.surequinos_backend.domain.entity.InstagramPost;
import com.surequinos.surequinos_backend.infrastructure.repository.InstagramPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InstagramPostService {

    private final InstagramPostRepository instagramPostRepository;

    public List<InstagramPostDto> getActivePosts() {
        log.debug("Obteniendo posts de Instagram activos");
        return instagramPostRepository.findByIsActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<InstagramPostDto> getAllPosts() {
        log.debug("Obteniendo todos los posts de Instagram");
        return instagramPostRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public InstagramPostDto createPost(InstagramPostDto dto) {
        log.info("Creando nuevo post de Instagram: {}", dto.getPostUrl());
        InstagramPost post = InstagramPost.builder()
                .postUrl(dto.getPostUrl())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .build();
        
        InstagramPost saved = instagramPostRepository.save(post);
        return mapToDto(saved);
    }

    @Transactional
    public InstagramPostDto updatePost(UUID id, InstagramPostDto dto) {
        log.info("Actualizando post de Instagram: {}", id);
        InstagramPost post = instagramPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post no encontrado"));
        
        post.setPostUrl(dto.getPostUrl());
        post.setIsActive(dto.getIsActive());
        post.setDisplayOrder(dto.getDisplayOrder());
        
        InstagramPost saved = instagramPostRepository.save(post);
        return mapToDto(saved);
    }

    @Transactional
    public void deletePost(UUID id) {
        log.info("Eliminando post de Instagram: {}", id);
        instagramPostRepository.deleteById(id);
    }

    private InstagramPostDto mapToDto(InstagramPost post) {
        return InstagramPostDto.builder()
                .id(post.getId())
                .postUrl(post.getPostUrl())
                .isActive(post.getIsActive())
                .displayOrder(post.getDisplayOrder())
                .build();
    }
}
