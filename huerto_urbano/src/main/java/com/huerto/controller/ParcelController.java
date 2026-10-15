package com.huerto.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.huerto.dto.ParcelDto;
import com.huerto.dto.ParcelRsDto;
import com.huerto.exception.DuplicateParcelException;
import com.huerto.exception.ParcelNotFoundException;
import com.huerto.service.ParcelService;

import jakarta.validation.Valid;

@Controller
@RequestMapping(value = "/api/v1/parcels")
public class ParcelController {

    private final ParcelService parcelService;

    public ParcelController(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<ParcelDto> parcelDtos = parcelService.findAll();
        return new ResponseEntity<>(parcelDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Integer id) throws ParcelNotFoundException {
        ParcelDto parcelDto = parcelService.findById(id);
        return new ResponseEntity<>(parcelDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody ParcelDto parcelDto) throws DuplicateParcelException {
        ParcelRsDto rsDto = parcelService.create(parcelDto);
        return new ResponseEntity<>(rsDto, HttpStatus.CREATED);
    }

}
