package com.cmp_huerto.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cmp_huerto.dto.ErrorMessageDto;
import com.cmp_huerto.dto.ParcelDto;
import com.cmp_huerto.dto.ParcelRsDto;
import com.cmp_huerto.exception.DuplicateParcelException;
import com.cmp_huerto.exception.ParcelNotFoundException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ParcelService {

    @Value("${base.url.parcels}")
    private String baseUrlParcels;

    private final WebClient webClient;

    public ParcelService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<ParcelDto> findAll() {
        return webClient.get()
                .uri(baseUrlParcels)
                .retrieve()
                .bodyToFlux(ParcelDto.class);
    }

    /**
     * Si el base responde 404, se convierte en ParcelNotFoundException con el mensaje del base.
     */
    public Mono<ParcelDto> findById(Integer id) {
        return webClient.get()
                .uri(baseUrlParcels + "/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(),
                        response -> response.bodyToMono(ErrorMessageDto.class)
                                .map(error -> ParcelNotFoundException.builder()
                                        .message(error.getMessage())
                                        .build()))
                .bodyToMono(ParcelDto.class);
    }

    /**
     * Si el base responde 409 (nombre repetido), se convierte en DuplicateParcelException con el mensaje del base.
     */
    public Mono<ParcelRsDto> create(ParcelDto dto) {
        return webClient.post()
                .uri(baseUrlParcels)
                .bodyValue(dto)
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.CONFLICT.value(),
                        response -> response.bodyToMono(ErrorMessageDto.class)
                                .map(error -> DuplicateParcelException.builder()
                                        .message(error.getMessage())
                                        .build()))
                .bodyToMono(ParcelRsDto.class);
    }

}
