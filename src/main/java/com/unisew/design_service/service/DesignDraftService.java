package com.unisew.design_service.service;

import com.unisew.design_service.models.DesignDraft;
import com.unisew.design_service.request.CreateDesignDraftRequest;
import com.unisew.design_service.request.SetDesignDraftFinalRequest;
import com.unisew.design_service.request.SubmitDeliveryRequest;
import com.unisew.design_service.response.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface DesignDraftService {
    ResponseEntity<ResponseObject> createDesignDraft(CreateDesignDraftRequest request);
    ResponseEntity<ResponseObject> setDesignDraftFinal(SetDesignDraftFinalRequest request);
    ResponseEntity<ResponseObject> submitDelivery(SubmitDeliveryRequest request);
}
