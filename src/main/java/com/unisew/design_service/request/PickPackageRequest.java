package com.unisew.design_service.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PickPackageRequest {
    int designRequestId;
    int packageId;
    long getPackagePrice;
    String packageName;
    String packageHeaderContent;
    int revisionTime;
    int packageDeliveryDate;
}
