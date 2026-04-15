package com.surequinos.surequinos_backend.infrastructure.repository;

import com.surequinos.surequinos_backend.domain.entity.NewsItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NewsItemRepository extends JpaRepository<NewsItem, UUID> {
    List<NewsItem> findByIsActiveTrueOrderByDisplayOrderAsc();
}
