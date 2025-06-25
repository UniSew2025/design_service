package com.unisew.design_service.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "design_comment")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "design_request_id", nullable = false)
    DesignRequest designRequest;

    @Column(name = "sender_id", nullable = false)
    Integer senderId;

    @Column(name = "sender_role", nullable = false)
    String senderRole;

    @Column(name = "content", nullable = false)
    String content;

    @Column(name = "creation_date", nullable = false)
    LocalDateTime creationDate;
}
