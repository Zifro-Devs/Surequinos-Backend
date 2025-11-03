package com.surequinos.surequinos_backend.application.service;

import com.surequinos.surequinos_backend.application.dto.CategoryDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateCategoryRequest;
import com.surequinos.surequinos_backend.application.mapper.CategoryMapper;
import com.surequinos.surequinos_backend.domain.entity.Category;
import com.surequinos.surequinos_backend.infrastructure.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para gestión de categorías
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Obtiene todas las categorías principales con sus subcategorías
     */
    public List<CategoryDto> getMainCategories() {
        log.debug("Obteniendo categorías principales");
        
        List<Category> categories = categoryRepository.findMainCategories();
        List<CategoryDto> categoryDtos = categoryMapper.toDtoList(categories);
        
        // Cargar subcategorías para cada categoría principal
        categoryDtos.forEach(this::loadSubcategories);
        
        return categoryDtos;
    }

    /**
     * Obtiene categorías principales con conteo de productos
     */
    public List<CategoryDto> getMainCategoriesWithProductCount() {
        log.debug("Obteniendo categorías principales con conteo de productos");
        
        List<Object[]> results = categoryRepository.findMainCategoriesWithProductCount();
        
        return results.stream()
            .map(this::mapCategoryWithProductCount)
            .toList();
    }

    /**
     * Busca una categoría por su slug
     */
    public Optional<CategoryDto> getCategoryBySlug(String slug) {
        log.debug("Buscando categoría por slug: {}", slug);
        
        return categoryRepository.findBySlug(slug)
            .map(category -> {
                CategoryDto dto = categoryMapper.toDto(category);
                loadSubcategories(dto);
                return dto;
            });
    }

    /**
     * Obtiene una categoría por ID
     */
    public Optional<CategoryDto> getCategoryById(UUID id) {
        log.debug("Buscando categoría por ID: {}", id);
        
        return categoryRepository.findById(id)
            .map(category -> {
                CategoryDto dto = categoryMapper.toDto(category);
                loadSubcategories(dto);
                return dto;
            });
    }

    /**
     * Crea una nueva categoría
     */
    @Transactional
    public CategoryDto createCategory(CreateCategoryRequest request) {
        log.debug("Creando nueva categoría: {}", request.getName());
        
        // Validar que el slug sea único
        if (categoryRepository.existsBySlugAndIdNot(request.getSlug(), null)) {
            throw new IllegalArgumentException("Ya existe una categoría con el slug: " + request.getSlug());
        }
        
        // Validar categoría padre si se especifica
        if (request.getParentId() != null) {
            if (!categoryRepository.existsById(request.getParentId())) {
                throw new IllegalArgumentException("La categoría padre no existe: " + request.getParentId());
            }
        }
        
        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);
        
        log.info("Categoría creada exitosamente: {} (ID: {})", savedCategory.getName(), savedCategory.getId());
        
        return categoryMapper.toDto(savedCategory);
    }

    /**
     * Actualiza una categoría existente
     */
    @Transactional
    public CategoryDto updateCategory(UUID id, CreateCategoryRequest request) {
        log.debug("Actualizando categoría ID: {}", id);
        
        Category existingCategory = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + id));
        
        // Validar que el slug sea único (excluyendo la categoría actual)
        if (categoryRepository.existsBySlugAndIdNot(request.getSlug(), id)) {
            throw new IllegalArgumentException("Ya existe una categoría con el slug: " + request.getSlug());
        }
        
        // Validar categoría padre si se especifica
        if (request.getParentId() != null) {
            if (!categoryRepository.existsById(request.getParentId())) {
                throw new IllegalArgumentException("La categoría padre no existe: " + request.getParentId());
            }
            // No permitir que una categoría sea padre de sí misma
            if (request.getParentId().equals(id)) {
                throw new IllegalArgumentException("Una categoría no puede ser padre de sí misma");
            }
        }
        
        // Actualizar campos
        existingCategory.setParentId(request.getParentId());
        existingCategory.setName(request.getName());
        existingCategory.setSlug(request.getSlug());
        existingCategory.setDisplayOrder(request.getDisplayOrder());
        
        Category savedCategory = categoryRepository.save(existingCategory);
        
        log.info("Categoría actualizada exitosamente: {} (ID: {})", savedCategory.getName(), savedCategory.getId());
        
        return categoryMapper.toDto(savedCategory);
    }

    /**
     * Elimina una categoría
     */
    @Transactional
    public void deleteCategory(UUID id) {
        log.debug("Eliminando categoría ID: {}", id);
        
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + id));
        
        // Verificar que no tenga subcategorías
        List<Category> subcategories = categoryRepository.findSubcategories(id);
        if (!subcategories.isEmpty()) {
            throw new IllegalStateException("No se puede eliminar la categoría porque tiene subcategorías");
        }
        
        // Verificar que no tenga productos (esto se podría hacer con una query)
        // Por simplicidad, dejamos que la BD maneje la restricción de FK
        
        categoryRepository.delete(category);
        
        log.info("Categoría eliminada exitosamente: {} (ID: {})", category.getName(), category.getId());
    }

    /**
     * Obtiene la jerarquía completa de categorías
     */
    public List<Object[]> getCategoryHierarchy() {
        log.debug("Obteniendo jerarquía completa de categorías");
        return categoryRepository.findCategoryHierarchy();
    }

    /**
     * Carga las subcategorías de una categoría
     */
    private void loadSubcategories(CategoryDto categoryDto) {
        if (categoryDto.getId() != null) {
            List<Category> subcategories = categoryRepository.findSubcategories(categoryDto.getId());
            categoryDto.setSubcategories(categoryMapper.toDtoList(subcategories));
        }
    }

    /**
     * Mapea el resultado de la query con conteo de productos
     */
    private CategoryDto mapCategoryWithProductCount(Object[] result) {
        // Manejar el conteo de productos que puede ser Long o BigInteger dependiendo de la BD
        Long productCount = 0L;
        if (result[6] != null) {
            if (result[6] instanceof Long) {
                productCount = (Long) result[6];
            } else if (result[6] instanceof BigInteger) {
                productCount = ((BigInteger) result[6]).longValue();
            } else if (result[6] instanceof Integer) {
                productCount = ((Integer) result[6]).longValue();
            } else {
                productCount = Long.valueOf(result[6].toString());
            }
        }
        
        return CategoryDto.builder()
            .id((UUID) result[0])
            .parentId((UUID) result[1])
            .name((String) result[2])
            .slug((String) result[3])
            .displayOrder((Integer) result[4])
            .createdAt(((java.sql.Timestamp) result[5]).toLocalDateTime())
            .productCount(productCount)
            .build();
    }
}