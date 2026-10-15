package com.cmp_huerto.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.server.ServerWebInputException;

import com.cmp_huerto.dto.ErrorMessageDto;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(ParcelNotFoundException.class)
    public ResponseEntity<?> parcelNotFoundExceptionHandler(ParcelNotFoundException parcelNotFoundException) {
        ErrorMessageDto errorMessageDto = ErrorMessageDto.builder()
                .httpStatus(HttpStatus.NOT_FOUND.toString())
                .message(parcelNotFoundException.getMessage())
                .build();
        return new ResponseEntity<>(errorMessageDto, HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(DuplicateParcelException.class)
    public ResponseEntity<?> duplicateParcelExceptionHandler(DuplicateParcelException duplicateParcelException) {
        ErrorMessageDto errorMessageDto = ErrorMessageDto.builder()
                .httpStatus(HttpStatus.CONFLICT.toString())
                .message(duplicateParcelException.getMessage())
                .build();
        return new ResponseEntity<>(errorMessageDto, HttpStatus.CONFLICT);
    }

    /**
     * Errores de las anotaciones de validación (@NotBlank, @Size, @SanitizedHtml...) del cuerpo con @Valid.
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<?> validationExceptionHandler(WebExchangeBindException validationException) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : validationException.getFieldErrors()) {
            // Un mensaje por campo: si un campo falla varias reglas, se informa la primera.
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ErrorMessageDto errorMessageDto = ErrorMessageDto.builder()
                .httpStatus(HttpStatus.BAD_REQUEST.toString())
                .message("Los datos enviados no son válidos")
                .errors(errors)
                .build();
        return new ResponseEntity<>(errorMessageDto, HttpStatus.BAD_REQUEST);
    }

    /**
     * Cuerpo vacío o mal formado, o parámetro de la URL con tipo incorrecto (p. ej. /api/v1/parcels/abc).
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<?> inputExceptionHandler(ServerWebInputException inputException) {
        String message = inputException.getCause() instanceof TypeMismatchException
                ? "El parámetro 'id' debe ser un número entero"
                : "El cuerpo de la petición no es un JSON válido o tiene tipos de dato incorrectos";
        ErrorMessageDto errorMessageDto = ErrorMessageDto.builder()
                .httpStatus(HttpStatus.BAD_REQUEST.toString())
                .message(message)
                .build();
        return new ResponseEntity<>(errorMessageDto, HttpStatus.BAD_REQUEST);
    }

    /**
     * El microservicio base no responde (apagado o fuera de tiempo).
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<?> baseUnavailableExceptionHandler(WebClientRequestException requestException) {
        ErrorMessageDto errorMessageDto = ErrorMessageDto.builder()
                .httpStatus(HttpStatus.SERVICE_UNAVAILABLE.toString())
                .message("El microservicio base de parcelas no está disponible")
                .build();
        return new ResponseEntity<>(errorMessageDto, HttpStatus.SERVICE_UNAVAILABLE);
    }

}
