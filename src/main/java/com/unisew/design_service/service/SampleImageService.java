package com.unisew.design_service.service;

import com.unisew.design_service.response.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface SampleImageService {
    ResponseEntity<ResponseObject> getAllSampleImages();
}
