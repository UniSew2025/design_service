package com.unisew.design_service.models;

import com.unisew.design_service.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "`design_request`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    LocalDate deadline;

    @Column(name = "`creation_date`")
    LocalDate creationDate;

    @Enumerated(EnumType.STRING)
    Status status;

    @Column(name = "`designer_id`")
    Integer designerId;

    @Column(name = "`school_id`")
    Integer schoolId;

    @OneToMany(mappedBy = "designRequest")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DesignRequestItem> designRequestItems;
}
