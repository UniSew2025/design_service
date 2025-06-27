package com.unisew.design_service.service;

import com.unisew.design_service.models.Cloth;
import com.unisew.design_service.models.DesignRequest;
import com.unisew.design_service.models.SampleImage;
import com.unisew.design_service.repositories.ClothRepo;
import com.unisew.design_service.repositories.DesignRequestRepo;
import com.unisew.design_service.repositories.SampleImageRepo;
import com.unisew.design_service.response.ResponseObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SampleImageServiceImpl implements SampleImageService {

    final SampleImageRepo sampleImageRepo;
    final ClothRepo clothRepo;
    final DesignRequestRepo designRequestRepo;

    @Override
    public ResponseEntity<ResponseObject> getAllSampleImages() {

        List<SampleImage> sampleImages = sampleImageRepo.findAll();


        if (sampleImages.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .message("Sample images not found")
                            .build()
            );
        }

        List<Map<String, Object>> mappedSampleImages = sampleImages.stream()
                .map(sampleImage -> {

                    Cloth cloth = clothRepo.findById(sampleImage.getCloth().getId()).orElse(null);
                    assert cloth != null;
                    DesignRequest designRequest = designRequestRepo.findById(cloth.getDesignRequest().getId()).orElse(null);
                    assert designRequest != null;
                    Map<String, Object> mappedDesign = new HashMap<>();
                    mappedDesign.put("id", designRequest.getId());
                    mappedDesign.put("createDate", designRequest.getCreationDate());
                    mappedDesign.put("isPrivate", designRequest.isPrivate());

                    Map<String, Object> map = new HashMap<>();
                    map.put("id", sampleImage.getId());
                    map.put("clothId", sampleImage.getCloth().getId());
                    map.put("url", sampleImage.getImageUrl());
                    map.put("designRequest", mappedDesign);
                    return map;
                }).toList();

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Sample images found")
                        .data(mappedSampleImages)
                        .build()
        );
    }
}
