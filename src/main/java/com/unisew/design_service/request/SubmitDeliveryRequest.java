package com.unisew.design_service.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SubmitDeliveryRequest {
    private Integer requestId;
    private List<DraftItem> drafts;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class DraftItem {
        private Integer clothId;
        private String descriptions;
        private List<ImageDto> images;

        @AllArgsConstructor
        @NoArgsConstructor
        @Data
        @Builder
        public static class ImageDto {
            private String url;
            private String name;
        }
    }
}
