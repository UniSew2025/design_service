package com.unisew.design_service.service;

import com.unisew.design_service.enums.Status;
import com.unisew.design_service.models.Cloth;
import com.unisew.design_service.models.DesignRequest;
import com.unisew.design_service.models.SampleImage;
import com.unisew.design_service.repositories.ClothRepo;
import com.unisew.design_service.repositories.DesignRequestRepo;
import com.unisew.design_service.repositories.SampleImageRepo;
import com.unisew.design_service.request.CreateDesignRequest;
import com.unisew.design_service.request.GetDesignRequestById;
import com.unisew.design_service.response.ResponseObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignRequestServiceImpl implements DesignRequestService {

    final DesignRequestRepo designRequestRepo;
    final ClothRepo clothRepo;
    final SampleImageRepo sampleImageRepo;

    @Override
    public ResponseEntity<ResponseObject> getAllDesignRequests() {
        // Change id to name after have another service
        List<Map<String, Object>> designRequests = designRequestRepo.findAll().stream().map(
                request -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", request.getId());
                    response.put("school", request.getSchoolId());
                    response.put("private", request.isPrivate());
                    response.put("feedback", request.getFeedbackId());
                    response.put("status", request.getStatus());
                    return response;
                }
        ).toList();

        if (designRequests.isEmpty()) {
            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .status("404")
                            .message("Design Request Empty")
                            .build()
            );
        }


        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .status("200")
                        .message("Get list of Design Requests")
                        .data(designRequests)
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> getDesignRequestById(GetDesignRequestById request) {

        DesignRequest designRequest = designRequestRepo.findById(request.getDesignId()).orElse(null);

        if (designRequest == null) {
            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .status("404")
                            .message("Can not find Design Request with " + request.getDesignId())
                            .build()
            );
        }

        Map<String, Object> map = new HashMap<>();
        map.put("id", designRequest.getId());
        map.put("school", designRequest.getSchoolId());
        map.put("private", designRequest.isPrivate());
        map.put("feedback", designRequest.getFeedbackId());
        map.put("status", designRequest.getStatus());


        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .status("200")
                        .message("Get Design Request successfully")
                        .data(map)
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> createDesignRequest(CreateDesignRequest request) {

        //validation null(school, designer)

        DesignRequest designRequest = DesignRequest.builder()
                .creationDate(LocalDate.now())
                .schoolId(request.getSchoolId())
                .isPrivate(true)
                .status(Status.DRAFT)
                .build();
        designRequestRepo.save(designRequest);

        List<Cloth> cloths = request.getClothes().stream()
                .map(clothRequest -> {
                    SampleImage sampleImage = sampleImageRepo.findById(clothRequest.getSampleImageId()).orElse(null);
                    Cloth template = null;

                    if (sampleImage == null) {
                        template = clothRepo.findById(clothRequest.getTemplateId()).orElse(null);
                    }
                    return Cloth.builder()
                            .template(template)
                            .designRequest(designRequest)
                            .type(clothRequest.getType())
                            .category(clothRequest.getCategory())
                            .logoImage(clothRequest.getLogoImage())
                            .logoHeight(clothRequest.getLogoHeight())
                            .logoWidth(clothRequest.getLogoWidth())
                            .color(clothRequest.getColor())
                            .note(clothRequest.getNote())
                            .build();
                }).toList();
        clothRepo.saveAll(cloths);


        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .status("201")
                        .message("Create Design Request successfully")
                        .build()
        );
    }
}
