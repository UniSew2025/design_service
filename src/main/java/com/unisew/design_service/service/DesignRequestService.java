package com.unisew.design_service.service;

import com.unisew.design_service.request.*;
import com.unisew.design_service.response.ResponseObject;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DesignRequestService {
    ResponseEntity<ResponseObject> getAllDesignRequests();
    ResponseEntity<ResponseObject> getDesignRequestById(GetDesignRequestById request);
    ResponseEntity<ResponseObject> createDesignRequest(CreateDesignRequest request);
    ResponseEntity<ResponseObject> pickPackage(PickPackageRequest request);
    ResponseEntity<ResponseObject> makeDesignPublic(MakeDesignPublicRequest request);
    ResponseEntity<ResponseObject> createRevisionDesign(CreateRevisionDesignRequest request);
    ResponseEntity<ResponseObject> getAllDesignComments(int designId);
    ResponseEntity<ResponseObject> getListDesignComplete();
    ResponseEntity<ResponseObject> sendComment(SendCommentRequest request);
    ResponseEntity<ResponseObject> getAllDesignByPackageId(GetAllDesignByPackageIdRequest request);
    ResponseEntity<ResponseObject> getAllDeliveryByRequestId(int requestId);
}
