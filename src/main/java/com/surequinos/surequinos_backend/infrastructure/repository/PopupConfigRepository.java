package com.surequinos.surequinos_backend.infrastructure.repository;

import com.surequinos.surequinos_backend.domain.entity.PopupConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PopupConfigRepository extends JpaRepository<PopupConfig, UUID> {
    
    @Query("SELECT p FROM PopupConfig p WHERE p.isActive = true ORDER BY p.updatedAt DESC")
    Optional<PopupConfig> findActiveConfig();
}
