package com.cmp_huerto.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cmp_huerto.dto.ParcelDto;
import com.cmp_huerto.dto.ParcelRsDto;
import com.cmp_huerto.service.ParcelService;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1/parcels")
public class ParcelController {

    private final ParcelService parcelService;

    public ParcelController(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    @GetMapping
    public Mono<ResponseEntity<List<ParcelDto>>> findAll() {
        return parcelService.findAll()
                .collectList()
                .map(parcelDtos -> new ResponseEntity<>(parcelDtos, HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ParcelDto>> findById(@PathVariable("id") Integer id) {
        return parcelService.findById(id)
                .map(parcelDto -> new ResponseEntity<>(parcelDto, HttpStatus.OK));
    }

    @PostMapping
    public Mono<ResponseEntity<ParcelRsDto>> save(@Valid @RequestBody ParcelDto parcelDto) {
        return parcelService.create(parcelDto)
                .map(rsDto -> new ResponseEntity<>(rsDto, HttpStatus.CREATED));
    }

}
