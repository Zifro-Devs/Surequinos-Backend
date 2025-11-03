package com.surequinos.surequinos_backend.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

/**
 * Configuración para Cloudflare R2 Storage
 */
@Configuration
@ConfigurationProperties(prefix = "cloudflare.r2")
@Data
public class CloudflareR2Config {

    private String accountId;
    private String accessKeyId;
    private String secretAccessKey;
    private String bucketName;
    private String region;
    private String endpoint;
    private String publicUrl;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        
        return S3Client.builder()
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .build();
    }
}