package com.unisew.design_service.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateDesignDraftRequest {
    int clothId;
    String descriptions;
    List<DraftImageRequest> images;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DraftImageRequest {
        String name;
        String url;
    }
}
