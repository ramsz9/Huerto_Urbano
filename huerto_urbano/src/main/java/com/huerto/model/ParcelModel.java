package com.huerto.model;

import java.time.LocalDateTime;

import com.huerto.dto.ParcelDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "parcel")
public class ParcelModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name", columnDefinition = "varchar(100)", unique = true)
    private String name;
    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "longitude")
    private Double longitude;
    @Enumerated(EnumType.STRING)
    @Column(name = "soil_type", columnDefinition = "varchar(20)")
    private SoilType soilType;
    @Column(name = "owner_or_lead", columnDefinition = "varchar(100)")
    private String ownerOrLead;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ParcelDto toDto() {
        return ParcelDto.builder()
                .id(this.id)
                .name(this.name)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .soilType(this.soilType.name())
                .ownerOrLead(this.ownerOrLead)
                .createdAt(this.createdAt)
                .build();
    }

}
