package com.unisew.design_service.models;

import com.unisew.design_service.enums.Status;
import jakarta.persistence.*;
import lombok.*;
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

    @Column(name = "`creation_date`")
    LocalDate creationDate;

    @Column(name = "`is_private`")
    boolean designPrivate;

    @Enumerated(EnumType.STRING)
    Status status;

    @Column(name = "`school_id`")
    Integer schoolId;

    @Column(name = "`package_id`")
    Integer packageId;

    @Column(name = "`package_name`")
    String packageName;

    @Column(name = "`package_header_content`")
    String packageHeaderContent;

    @Column(name = "`package_delivery_date`")
    int packageDeliveryDate;

    @Column(name = "`revision_time`")
    int revisionTime;

    @Column(name = "`package_price`")
    long packagePrice;

    @OneToMany(mappedBy = "designRequest")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DesignItem> designItems;

    @Column(name = "`feedback_id`")
    Integer feedbackId;

    @OneToMany(mappedBy = "designRequest")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DesignComment> comments;

    @OneToMany(mappedBy = "designRequest")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DesignDelivery> deliveries;
}
