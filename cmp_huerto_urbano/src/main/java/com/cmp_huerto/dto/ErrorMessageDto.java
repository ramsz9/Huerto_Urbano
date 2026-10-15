package com.cmp_huerto.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMessageDto {
    private String message;
    private String httpStatus;
    /**
     * Mensaje de error por campo. Solo se envía cuando falla la validación del cuerpo.
     */
    private Map<String, String> errors;
}
