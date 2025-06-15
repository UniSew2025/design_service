package com.unisew.design_service.request;

import com.unisew.design_service.enums.ClothCategory;
import com.unisew.design_service.enums.ClothType;
import com.unisew.design_service.enums.Status;
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
    int designerId;
    int schoolId;
    List<ClothRequest> clothes;


    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ClothRequest {
        int templateId;
        int sampleImageId;
        ClothType type;
        ClothCategory category;
        String logoImage;
        String logoPosition;
        int logoHeight;
        int logoWidth;
        String color;
        String note;
    }
}
