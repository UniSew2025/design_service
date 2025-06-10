package com.unisew.design_service.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`garment_declaration`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GarmentDeclaration {

    @Id
    @Column(name = "`garment_id`")
    Integer id;

    @Column(name = "`modification_date`")
    LocalDate modificationDate;

    @Column(name = "`daily_sewing_capacity`")
    int dailySewingCapacity;

    @Column(name = "`garment_id`")
    Integer garmentId;

    @OneToMany(mappedBy = "garmentDeclaration")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DeclarationItem> declarationItems;
}
