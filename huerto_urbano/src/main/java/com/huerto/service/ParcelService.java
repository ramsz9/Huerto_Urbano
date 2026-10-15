package com.huerto.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.huerto.dto.ParcelDto;
import com.huerto.dto.ParcelRsDto;
import com.huerto.exception.DuplicateParcelException;
import com.huerto.exception.ParcelNotFoundException;
import com.huerto.model.ParcelModel;
import com.huerto.repository.ParcelRepository;

@Service
public class ParcelService {

    private final ParcelRepository parcelRepository;

    public ParcelService(ParcelRepository parcelRepository) {
        this.parcelRepository = parcelRepository;
    }

    public List<ParcelDto> findAll() {
        return parcelRepository.findAll().stream().map(ParcelModel::toDto).toList();
    }

    public ParcelDto findById(Integer id) throws ParcelNotFoundException {
        ParcelModel parcelModel = parcelRepository.findById(id)
                .orElseThrow(() -> ParcelNotFoundException.builder()
                        .message("No existe una parcela con el id " + id)
                        .build());
        return parcelModel.toDto();
    }

    public ParcelRsDto create(ParcelDto dto) throws DuplicateParcelException {
        if (parcelRepository.existsByNameIgnoreCase(dto.getName())) {
            throw DuplicateParcelException.builder()
                    .message("Ya existe una parcela con el nombre " + dto.getName())
                    .build();
        }
        dto.setCreatedAt(LocalDateTime.now());
        Integer id = parcelRepository.save(dto.toModel()).getId();
        return new ParcelRsDto("Parcela registrada", id);
    }

}
