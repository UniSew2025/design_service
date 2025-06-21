package com.unisew.design_service.service;

import com.unisew.design_service.request.GetAllClothByRequestId;
import com.unisew.design_service.response.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface ClothService {
    ResponseEntity<ResponseObject> getAllClothesByRequestId(GetAllClothByRequestId request);
    ResponseEntity<ResponseObject> getAllClothes();
}
