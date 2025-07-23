package com.unisew.design_service.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "`design_delivery`")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "`design_request_id`")
    DesignRequest designRequest;

    @Column(name = "`file_url`")
    String fileUrl;

    @Column(name = "`delivery_number`")
    Integer deliveryNumber;

    @Column(name = "`submit_date`")
    LocalDateTime submitDate;

    @Column(name = "`is_final`")
    boolean designFinal;

    String note;

    @Column(name = "`is_revision`")
    boolean revision;

    @OneToMany(mappedBy = "delivery")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<RevisionRequest> revisionRequests;

    @OneToOne
    @JoinColumn(name = "`revision_id`")
    RevisionRequest parentRevision;
}
