package com.unisew.design_service.service;

import com.unisew.design_service.request.CreateDesignRequest;
import com.unisew.design_service.request.GetDesignRequestById;
import com.unisew.design_service.response.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface DesignRequestService {
    ResponseEntity<ResponseObject> getAllDesignRequests();
    ResponseEntity<ResponseObject> getDesignRequestById(GetDesignRequestById request);
    ResponseEntity<ResponseObject> createDesignRequest(CreateDesignRequest request);
    //
}
