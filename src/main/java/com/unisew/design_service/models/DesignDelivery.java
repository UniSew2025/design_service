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

    String fileUrl;
    Integer deliveryNumber;
    LocalDateTime submitDate;
    boolean isFinal;
    String note;
    boolean isRevision;

    @OneToMany(mappedBy = "delivery")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<RevisionRequest> revisionRequests;

    @OneToOne
    @JoinColumn(name = "`revision_id`")
    RevisionRequest parentRevision;
}
