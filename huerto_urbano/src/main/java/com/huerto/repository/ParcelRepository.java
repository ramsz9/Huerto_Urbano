package com.huerto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.huerto.model.ParcelModel;

@Repository
public interface ParcelRepository extends JpaRepository<ParcelModel, Integer> {

    boolean existsByNameIgnoreCase(String name);

}
