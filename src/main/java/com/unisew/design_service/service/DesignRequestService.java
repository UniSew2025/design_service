package com.unisew.design_service.service;

import com.unisew.design_service.request.*;
import com.unisew.design_service.response.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface DesignRequestService {
    ResponseEntity<ResponseObject> getAllDesignRequests();
    ResponseEntity<ResponseObject> getDesignRequestById(GetDesignRequestById request);
    ResponseEntity<ResponseObject> createDesignRequest(CreateDesignRequest request);
    ResponseEntity<ResponseObject> pickPackage(PickPackageRequest request);
    ResponseEntity<ResponseObject> makeDesignPublic(MakeDesignPublicRequest request);
    ResponseEntity<ResponseObject> createRevisionDesign(CreateRevisionDesignRequest request);
}
