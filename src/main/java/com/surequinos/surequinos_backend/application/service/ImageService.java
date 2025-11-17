package com.surequinos.surequinos_backend.application.service;

import com.surequinos.surequinos_backend.infrastructure.config.CloudflareR2Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestión de imágenes en Cloudflare R2
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final S3Client s3Client;
    private final CloudflareR2Config r2Config;

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
        "image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * Sube una imagen de producto principal
     */
    public String uploadProductImage(MultipartFile file, UUID productId) throws IOException {
        validateFile(file);
        
        String fileName = generateProductImageFileName(productId, file.getOriginalFilename());
        String key = "products/" + fileName;
        
        return uploadFile(file, key);
    }

    /**
     * Sube múltiples imágenes de producto
     */
    public List<String> uploadProductImages(List<MultipartFile> files, UUID productId) throws IOException {
        return files.stream()
            .map(file -> {
                try {
                    return uploadProductImage(file, productId);
                } catch (IOException e) {
                    log.error("Error subiendo imagen de producto {}: {}", productId, e.getMessage());
                    throw new RuntimeException("Error subiendo imagen", e);
                }
            })
            .toList();
    }

    /**
     * Sube una imagen específica de variante
     */
    public String uploadVariantImage(MultipartFile file, UUID productId, String variantSku) throws IOException {
        log.info("Iniciando subida de imagen de variante - SKU: {}, Producto: {}, Archivo: {}", 
                variantSku, productId, file.getOriginalFilename());
        
        validateFile(file);
        
        String fileName = generateVariantImageFileName(productId, variantSku, file.getOriginalFilename());
        String key = "variants/" + fileName;
        
        log.info("Nombre de archivo generado: {}, Key: {}", fileName, key);
        
        String url = uploadFile(file, key);
        
        log.info("Imagen de variante subida exitosamente - URL: {}", url);
        
        return url;
    }

    /**
     * Elimina una imagen del storage
     */
    public void deleteImage(String imageUrl) {
        try {
            String key = extractKeyFromUrl(imageUrl);
            
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(r2Config.getBucketName())
                .key(key)
                .build();
            
            s3Client.deleteObject(deleteRequest);
            log.info("Imagen eliminada exitosamente: {}", key);
            
        } catch (Exception e) {
            log.error("Error eliminando imagen {}: {}", imageUrl, e.getMessage());
            throw new RuntimeException("Error eliminando imagen", e);
        }
    }

    /**
     * Elimina múltiples imágenes
     */
    public void deleteImages(List<String> imageUrls) {
        imageUrls.forEach(this::deleteImage);
    }

    /**
     * Obtiene la URL pública de una imagen
     */
    public String getPublicUrl(String key) {
        return r2Config.getPublicUrl() + "/" + key;
    }

    /**
     * Verifica si una imagen existe
     */
    public boolean imageExists(String imageUrl) {
        try {
            String key = extractKeyFromUrl(imageUrl);
            
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                .bucket(r2Config.getBucketName())
                .key(key)
                .build();
            
            s3Client.headObject(headRequest);
            return true;
            
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            log.error("Error verificando existencia de imagen {}: {}", imageUrl, e.getMessage());
            return false;
        }
    }

    /**
     * Sube un archivo al storage
     */
    private String uploadFile(MultipartFile file, String key) throws IOException {
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(r2Config.getBucketName())
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            String publicUrl = getPublicUrl(key);
            log.info("Imagen subida exitosamente: {} -> {}", key, publicUrl);
            
            return publicUrl;
            
        } catch (Exception e) {
            log.error("Error subiendo archivo {}: {}", key, e.getMessage());
            throw new RuntimeException("Error subiendo archivo", e);
        }
    }

    /**
     * Valida el archivo antes de subirlo
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido (10MB)");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Tipo de archivo no permitido. Solo se permiten: " + 
                String.join(", ", ALLOWED_CONTENT_TYPES));
        }
    }

    /**
     * Genera nombre único para imagen de producto
     */
    private String generateProductImageFileName(UUID productId, String originalFileName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String extension = getFileExtension(originalFileName);
        return String.format("product_%s_%s_%s.%s", 
            productId.toString().substring(0, 8), 
            timestamp, 
            UUID.randomUUID().toString().substring(0, 8),
            extension);
    }

    /**
     * Genera nombre único para imagen de variante
     */
    private String generateVariantImageFileName(UUID productId, String variantSku, String originalFileName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String extension = getFileExtension(originalFileName);
        String safeSku = variantSku.replaceAll("[^a-zA-Z0-9-_]", "_");
        return String.format("variant_%s_%s_%s_%s.%s", 
            productId.toString().substring(0, 8),
            safeSku,
            timestamp, 
            UUID.randomUUID().toString().substring(0, 8),
            extension);
    }

    /**
     * Extrae la extensión del archivo
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "jpg";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Extrae la key del storage desde una URL pública
     */
    private String extractKeyFromUrl(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith(r2Config.getPublicUrl())) {
            throw new IllegalArgumentException("URL de imagen inválida");
        }
        return imageUrl.substring(r2Config.getPublicUrl().length() + 1);
    }
}