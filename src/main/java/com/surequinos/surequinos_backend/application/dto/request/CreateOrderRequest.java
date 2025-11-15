package com.surequinos.surequinos_backend.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO para crear una nueva orden
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear una nueva orden")
public class CreateOrderRequest {

    @NotBlank(message = "El correo electrónico del cliente es obligatorio")
    @Email(message = "El correo electrónico debe ser válido")
    @Schema(description = "Correo electrónico del cliente", example = "cliente@example.com")
    private String email;

    @NotBlank(message = "El número de documento del cliente es obligatorio")
    @Schema(description = "Número de documento de identidad del cliente", example = "1234567890")
    private String documentNumber;

    @Schema(description = "Nombre completo del cliente (opcional, se usa si se crea un nuevo usuario)", example = "Juan Pérez")
    private String clientName;

    @Schema(description = "Número de teléfono del cliente (opcional, se usa si se crea un nuevo usuario)", example = "+57 300 1234567")
    private String clientPhoneNumber;

    @PositiveOrZero(message = "El valor del descuento no puede ser negativo")
    @Schema(description = "Valor del descuento aplicado", example = "50000.00")
    @Builder.Default
    private BigDecimal discountValue = BigDecimal.ZERO;

    @Schema(description = "Notas adicionales de la orden")
    private String notes;

    @NotBlank(message = "El método de pago es obligatorio")
    @Schema(description = "Método de pago utilizado", example = "TARJETA_CREDITO", 
            allowableValues = {"TARJETA_CREDITO", "TRANSFERENCIA_BANCARIA", "EFECTIVO", "CONTRAENTREGA", "NEQUI", "DAVIPLATA"})
    private String paymentMethod;

    @NotBlank(message = "La dirección de envío es obligatoria")
    @Schema(description = "Dirección de envío", example = "Calle 123 #45-67, Barrio Centro")
    private String shippingAddress;

    @PositiveOrZero(message = "El valor del envío no puede ser negativo")
    @Schema(description = "Valor del envío", example = "15000.00")
    @Builder.Default
    private BigDecimal shippingValue = BigDecimal.ZERO;

    @NotEmpty(message = "La orden debe tener al menos un item")
    @Schema(description = "Items de la orden")
    @Valid
    private List<CreateOrderItemRequest> items;
}

