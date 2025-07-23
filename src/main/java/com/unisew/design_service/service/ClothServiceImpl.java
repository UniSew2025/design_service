package com.unisew.design_service.service;

import com.unisew.design_service.models.DesignItem;
import com.unisew.design_service.models.SampleImage;
import com.unisew.design_service.repositories.DesignItemRepo;
import com.unisew.design_service.repositories.SampleImageRepo;
import com.unisew.design_service.request.GetAllClothByRequestId;
import com.unisew.design_service.response.ResponseObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClothServiceImpl implements ClothService {

    final DesignItemRepo designItemRepo;
    final SampleImageRepo sampleImageRepo;

    @Override
    public ResponseEntity<ResponseObject> getAllClothesByRequestId(GetAllClothByRequestId request) {

        List<DesignItem> designItemList = designItemRepo.getAllByDesignRequest_Id(request.getRequestId());

        if (designItemList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .message("no cloth found")
                            .build()
            );
        }

        List<Map<String, Object>> mappedCloths = designItemList.stream().map(cloth -> {
            Map<String, Object> map = new HashMap<>();
            map.put("logo_height", cloth.getLogoHeight());
            map.put("logo_width", cloth.getLogoWidth());
            map.put("template_id", cloth.getTemplate() != null ? cloth.getTemplate().getId() : null);
            map.put("cloth_category", cloth.getCategory());
            map.put("cloth_type", cloth.getType());
            map.put("color", cloth.getColor());
            map.put("fabric", cloth.getFabric());
            map.put("logo_image", cloth.getLogoImage());
            map.put("logo_position", cloth.getLogoPosition());
            map.put("note", cloth.getNote());

            List<SampleImage> sampleImages = sampleImageRepo.findAllByDesignItem_Id(cloth.getId());

            List<Map<String,Object>> mapImages = sampleImages.stream()
                    .map(
                            sampleImage -> {
                                Map<String, Object> image = new HashMap<>();
                                image.put("id", sampleImage.getId());
                                image.put("url", sampleImage.getImageUrl());
                                return image;
                            }
                    ).toList();
            map.put("images", mapImages);

            return map;
        }).toList();


        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Cloth list found")
                        .data(mappedCloths)
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> getAllClothes() {

        List<DesignItem> designItems = designItemRepo.findAll();

        if (designItems.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .message("no cloth found")
                            .build()
            );
        }

        List<Map<String, Object>> mappedCloths = designItems.stream()
                .map(cloth -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("logo_height", cloth.getLogoHeight());
                    map.put("logo_width", cloth.getLogoWidth());
                    map.put("template_id", cloth.getTemplate() != null ? cloth.getTemplate().getId() : null);
                    map.put("cloth_category", cloth.getCategory());
                    map.put("cloth_type", cloth.getType());
                    map.put("color", cloth.getColor());
                    map.put("fabric", cloth.getFabric());
                    map.put("logo_image", cloth.getLogoImage());
                    map.put("logo_position", cloth.getLogoPosition());
                    map.put("note", cloth.getNote());
                    return map;
                }).toList();


        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Cloth list found")
                        .data(mappedCloths)
                        .build()
        );
    }
}
