package com.unisew.design_service.models;

import com.unisew.design_service.enums.ClothCategory;
import com.unisew.design_service.enums.ClothType;
import com.unisew.design_service.enums.Fabric;
import com.unisew.design_service.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`design_item`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "`cloth_type`")
    ClothType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "`cloth_category`")
    ClothCategory category;

    @Column(name = "`logo_image`")
    String logoImage;

    @Column(name = "`logo_position`")
    String logoPosition;

    @Column(name = "`logo_width`")
    double logoWidth;

    @Column(name = "`logo_height`")
    double logoHeight;

    String color;

    @Enumerated(EnumType.STRING)
    Fabric fabric;

    String note;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @ManyToOne
    @JoinColumn(name = "`request_id`")
    DesignRequest designRequest;

    @OneToMany(mappedBy = "designItem")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<SampleImage> sampleImages;

    @ManyToOne
    @JoinColumn(name = "template_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    DesignItem template;

    @OneToMany(mappedBy = "designItem")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<FinalImage> finalImages;
}
