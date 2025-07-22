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
    private String fileUrl;
    private String note;
    private boolean isRevision;
    private Integer revisionId;
}
