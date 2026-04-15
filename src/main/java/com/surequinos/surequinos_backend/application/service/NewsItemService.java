package com.surequinos.surequinos_backend.application.service;

import com.surequinos.surequinos_backend.application.dto.NewsItemDto;
import com.surequinos.surequinos_backend.domain.entity.NewsItem;
import com.surequinos.surequinos_backend.infrastructure.repository.NewsItemRepository;
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
public class NewsItemService {

    private final NewsItemRepository newsItemRepository;

    public List<NewsItemDto> getActiveNews() {
        log.debug("Obteniendo noticias activas");
        return newsItemRepository.findByIsActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<NewsItemDto> getAllNews() {
        log.debug("Obteniendo todas las noticias");
        return newsItemRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public NewsItemDto createNews(NewsItemDto dto) {
        log.info("Creando nueva noticia: {}", dto.getTitle());
        NewsItem newsItem = NewsItem.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .linkUrl(dto.getLinkUrl())
                .dateText(dto.getDateText())
                .isFeatured(dto.getIsFeatured() != null ? dto.getIsFeatured() : false)
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .build();
        
        NewsItem saved = newsItemRepository.save(newsItem);
        return mapToDto(saved);
    }

    @Transactional
    public NewsItemDto updateNews(UUID id, NewsItemDto dto) {
        log.info("Actualizando noticia: {}", id);
        NewsItem newsItem = newsItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Noticia no encontrada"));
        
        newsItem.setTitle(dto.getTitle());
        newsItem.setDescription(dto.getDescription());
        newsItem.setImageUrl(dto.getImageUrl());
        newsItem.setLinkUrl(dto.getLinkUrl());
        newsItem.setDateText(dto.getDateText());
        newsItem.setIsFeatured(dto.getIsFeatured());
        newsItem.setIsActive(dto.getIsActive());
        newsItem.setDisplayOrder(dto.getDisplayOrder());
        
        NewsItem saved = newsItemRepository.save(newsItem);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteNews(UUID id) {
        log.info("Eliminando noticia: {}", id);
        newsItemRepository.deleteById(id);
    }

    private NewsItemDto mapToDto(NewsItem newsItem) {
        return NewsItemDto.builder()
                .id(newsItem.getId())
                .title(newsItem.getTitle())
                .description(newsItem.getDescription())
                .imageUrl(newsItem.getImageUrl())
                .linkUrl(newsItem.getLinkUrl())
                .dateText(newsItem.getDateText())
                .isFeatured(newsItem.getIsFeatured())
                .isActive(newsItem.getIsActive())
                .displayOrder(newsItem.getDisplayOrder())
                .build();
    }
}
