package com.cmp_huerto.dto;

import java.time.LocalDateTime;

import com.cmp_huerto.constraints.SanitizedHtml;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objeto de transferencia de datos de una parcela (zona de cultivo monitoreada).
 * El compose lo valida al recibirlo y lo reenvía al microservicio base; también
 * recibe en él las parcelas que devuelve el base en las consultas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcelDto {
    /**
     * Identificador de la parcela. Lo asigna el microservicio base, que lo ignora si el cliente lo envía.
     */
    private Integer id;

    @NotBlank(message = "El nombre de la parcela es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre de la parcela debe tener entre 3 y 100 caracteres")
    @SanitizedHtml(message = "El nombre de la parcela contiene código HTML no permitido")
    private String name;

    /**
     * Latitud de la coordenada GPS, en grados decimales.
     */
    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    private Double latitude;

    /**
     * Longitud de la coordenada GPS, en grados decimales.
     */
    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    private Double longitude;

    @NotBlank(message = "El tipo de suelo es obligatorio")
    @Pattern(regexp = "ARCILLOSO|LIMOSO|ARENOSO", message = "El tipo de suelo debe ser ARCILLOSO, LIMOSO o ARENOSO")
    private String soilType;

    @NotBlank(message = "El responsable de la parcela es obligatorio")
    @Size(min = 3, max = 100, message = "El responsable de la parcela debe tener entre 3 y 100 caracteres")
    @SanitizedHtml(message = "El responsable de la parcela contiene código HTML no permitido")
    private String ownerOrLead;

    /**
     * Fecha de registro. La asigna el microservicio base, que la ignora si el cliente la envía.
     */
    private LocalDateTime createdAt;

}
