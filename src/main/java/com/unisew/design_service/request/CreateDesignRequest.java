package com.unisew.design_service.request;

import com.unisew.design_service.enums.ClothCategory;
import com.unisew.design_service.enums.ClothType;
import com.unisew.design_service.enums.Status;
import com.unisew.design_service.models.SampleImage;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateDesignRequest {
    int schoolId;
    List<Cloth> clothes;
    String designType;



    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Cloth {
        List<Image> images;
        int templateId;
        ClothType type;
        ClothCategory category;
        String logoImage;
        String logoPosition;
        String color;
        String note;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Image {
        String url;
    }

}
