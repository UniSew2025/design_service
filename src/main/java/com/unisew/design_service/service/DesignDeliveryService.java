package com.unisew.design_service.service;

import com.unisew.design_service.request.AddFinalImagesRequest;
import com.unisew.design_service.request.SubmitDeliveryRequest;
import com.unisew.design_service.response.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface DesignDeliveryService {
    ResponseEntity<ResponseObject> submitDelivery(SubmitDeliveryRequest request);
    ResponseEntity<ResponseObject> getAllDeliveryByRequestId(int requestId);
    ResponseEntity<ResponseObject> getAllUnUsedRevisionRequest(int requestId);
    ResponseEntity<ResponseObject> makeDeliveryFinalAndRequestComplete(int deliveryId, int requestId);
    ResponseEntity<ResponseObject> AddFinalImages(AddFinalImagesRequest request);
    ResponseEntity<ResponseObject> getAllFinalImagesByRequestId(int requestId);
}
