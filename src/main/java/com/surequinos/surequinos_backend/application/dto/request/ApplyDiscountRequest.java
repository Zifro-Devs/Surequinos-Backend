package com.surequinos.surequinos_backend.application.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyDiscountRequest {
    
    @NotNull(message = "El ID de la variante es requerido")
    private UUID variantId;
    
    @NotNull(message = "El porcentaje de descuento es requerido")
    @DecimalMin(value = "0.01", message = "El descuento debe ser mayor a 0")
    @DecimalMax(value = "99.99", message = "El descuento debe ser menor a 100")
    private BigDecimal discountPercentage;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
}
