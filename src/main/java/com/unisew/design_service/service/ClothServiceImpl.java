package com.unisew.design_service.service;

import com.unisew.design_service.models.Cloth;
import com.unisew.design_service.repositories.ClothRepo;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClothServiceImpl implements ClothService {

    final ClothRepo clothRepo;

    @Override
    public ResponseEntity<ResponseObject> getAllClothesByRequestId(GetAllClothByRequestId request) {

        List<Cloth> clothList = clothRepo.getAllByDesignRequest_Id(request.getRequestId());

        if(clothList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                    ResponseObject.builder()
                            .message("no cloth found")
                            .build()
            );
        }

        List<Map<String, Object>> mappedCloths = clothList.stream().map(cloth -> {
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
