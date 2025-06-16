package com.unisew.design_service.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRevisionDesignRequest {
    int clothId;
    int designDraftId;
    String note;
}
