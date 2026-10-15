package com.huerto.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.huerto.dto.ErrorMessageDto;

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
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validationExceptionHandler(MethodArgumentNotValidException validationException) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : validationException.getBindingResult().getFieldErrors()) {
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
     * Cuerpo vacío, JSON mal formado o un tipo de dato incorrecto (p. ej. texto en la latitud).
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> messageNotReadableExceptionHandler(HttpMessageNotReadableException notReadableException) {
        ErrorMessageDto errorMessageDto = ErrorMessageDto.builder()
                .httpStatus(HttpStatus.BAD_REQUEST.toString())
                .message("El cuerpo de la petición no es un JSON válido o tiene tipos de dato incorrectos")
                .build();
        return new ResponseEntity<>(errorMessageDto, HttpStatus.BAD_REQUEST);
    }

    /**
     * Parámetro de la URL con tipo incorrecto (p. ej. /api/v1/parcels/abc).
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> typeMismatchExceptionHandler(MethodArgumentTypeMismatchException typeMismatchException) {
        ErrorMessageDto errorMessageDto = ErrorMessageDto.builder()
                .httpStatus(HttpStatus.BAD_REQUEST.toString())
                .message("El parámetro '" + typeMismatchException.getName() + "' debe ser un número entero")
                .build();
        return new ResponseEntity<>(errorMessageDto, HttpStatus.BAD_REQUEST);
    }

}
