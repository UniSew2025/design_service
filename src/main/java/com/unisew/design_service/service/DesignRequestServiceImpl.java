package com.unisew.design_service.service;

import com.unisew.design_service.enums.Fabric;
import com.unisew.design_service.enums.Gender;
import com.unisew.design_service.enums.Status;
import com.unisew.design_service.models.*;
import com.unisew.design_service.repositories.*;
import com.unisew.design_service.request.*;
import com.unisew.design_service.response.ResponseObject;
import com.unisew.design_service.utils.GetCurrentLoginUser;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignRequestServiceImpl implements DesignRequestService {

    final DesignRequestRepo designRequestRepo;
    final ClothRepo clothRepo;
    final SampleImageRepo sampleImageRepo;
    final DesignDeliveryRepo designDeliveryRepo;
    final RevisionRequestRepo revisionRequestRepo;
    private final DesignCommentRepo designCommentRepo;
    private final FinalImageRepo finalImageRepo;

    @Override
    public ResponseEntity<ResponseObject> getAllDesignRequests() {
        // Change id to name after have another service
        List<Map<String, Object>> designRequests = designRequestRepo.findAll().stream().map(
                request -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", request.getId());
                    response.put("school", request.getSchoolId());
                    response.put("private", request.isPrivate());
                    response.put("package", request.getPackageId());
                    response.put("feedback", request.getFeedbackId());
                    response.put("status", request.getStatus().getValue());
                    return response;
                }
        ).toList();

        if (designRequests.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Design Request Empty")
                            .build()
            );
        }


        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Get list of Design Requests")
                        .data(designRequests)
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> getDesignRequestById(GetDesignRequestById request) {

        DesignRequest designRequest = designRequestRepo.findById(request.getDesignId()).orElse(null);

        if (designRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
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


        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
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
                .status(Status.CREATED)
                .build();
        designRequestRepo.save(designRequest);

        for (CreateDesignRequest.Cloth cloth : request.getClothes()) {
            Cloth newCloth = clothRepo.save(Cloth.builder()
                    .type(cloth.getType())
                    .gender(Gender.valueOf(cloth.getGender()))
                    .category(cloth.getCategory())
                    .logoImage(cloth.getLogoImage())
                    .logoPosition(cloth.getLogoPosition())
                    .fabric(Fabric.valueOf(cloth.getFabric()))
                    .designRequest(designRequest)
                    .color(cloth.getColor())
                    .note(cloth.getNote())
                    .build());

            if (cloth.getDesignType().equals("TEMPLATE")) {

                Cloth template = clothRepo.findById(cloth.getTemplateId()).orElse(null);
                if (template == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            ResponseObject.builder()
                                    .message("Template Not Found")
                                    .build()
                    );
                }
                newCloth.setTemplate(template);
                clothRepo.save(newCloth);
            }

            if (cloth.getDesignType().equals("UPLOAD")) {
                createSampleImageByCloth(newCloth, cloth.getImages());
            }
        }


        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .message("Create Design Request successfully")
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> pickPackage(PickPackageRequest request) {

        DesignRequest designRequest = designRequestRepo.findById(request.getDesignRequestId()).orElse(null);

        if (designRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Can not find Design Request with " + request.getDesignRequestId())
                            .build()
            );
        }

        if (designRequest.getPackageId() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("This request already have package. Can not pick again!")
                            .build()
            );
        }

        designRequest.setPackageId(request.getPackageId());
        designRequest.setStatus(Status.UNPAID);
        designRequestRepo.save(designRequest);

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Pick package success ")
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> makeDesignPublic(MakeDesignPublicRequest request) {

        DesignRequest designRequest = designRequestRepo.findById(request.getDesignRequestId()).orElse(null);

        if (designRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Can not find Design Request with " + request.getDesignRequestId())
                            .build()
            );
        }

        designRequest.setPrivate(false);
        designRequestRepo.save(designRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Change design to public successfully")
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> createRevisionDesign(CreateRevisionDesignRequest request) {

        Integer senderId = GetCurrentLoginUser.getId();
        String senderRole = GetCurrentLoginUser.getRole();


        Optional<DesignDelivery> optDelivery = designDeliveryRepo.findById(request.getDeliveryId());
        if (optDelivery.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("DesignDelivery not found")
                            .build()
            );
        }
        DesignDelivery delivery = optDelivery.get();

        if (Boolean.TRUE
                .equals(delivery.getIsFinal())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Cannot create revision: Delivery has been finalized.")
                            .build()
            );
        }

        boolean existsRevision = revisionRequestRepo.existsByDelivery_Id(delivery.getId());
        if (existsRevision) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("A revision request already exists for this delivery.")
                            .build()
            );
        }

        RevisionRequest revisionRequest = RevisionRequest.builder()
                .delivery(delivery)
                .note(request.getNote())
                .createdAt(LocalDate.now())
                .build();
        revisionRequestRepo.save(revisionRequest);

        DesignComment sysComment = DesignComment.builder()
                .designRequest(delivery.getDesignRequest())
                .senderId(0)
                .senderRole("system")
                .content("School requested a revision: " + request.getNote())
                .creationDate(LocalDateTime.now())
                .build();
        designCommentRepo.save(sysComment);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .message("Create revision successfully")
                        .build()
        );
    }


    @Override
    public ResponseEntity<ResponseObject> getAllDesignComments(int designId) {

        List<DesignComment> designComments = designCommentRepo.findAllByDesignRequest_Id(designId);

        if (designComments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .message("This Conversation are empty please wait designer submit first")
                            .build()
            );
        }

        List<Map<String, Object>> mapList = designComments.stream()
                .map(
                        comment -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("senderId", comment.getSenderId());
                            map.put("senderRole", comment.getSenderRole());
                            map.put("content", comment.getContent());
                            map.put("createdAt", comment.getCreationDate());
                            return map;
                        }
                ).toList();

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("List of Design Comments")
                        .data(mapList)
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> getListDesignComplete() {

        Integer accountId = GetCurrentLoginUser.getId();
        String role = GetCurrentLoginUser.getRole();


        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Invalid header")
                            .build()
            );
        }

        List<DesignRequest> completeList = designRequestRepo.findAllByStatusAndSchoolId(Status.COMPLETED, accountId);

        if (completeList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .message("No Design Requests found")
                            .build()
            );
        }

        List<Map<String, Object>> mapList = completeList.stream().map(request -> {
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("id", request.getId());
            requestMap.put("creationDate", request.getCreationDate());
            requestMap.put("status", request.getStatus());
            requestMap.put("school", request.getSchoolId());

            DesignDelivery delivery = designDeliveryRepo.findByDesignRequest_IdAndIsFinalTrue(request.getId());

            requestMap.put("deliveryUrl", delivery == null ? null : delivery.getFileUrl());

            List<Cloth> cloths = clothRepo.getAllByDesignRequest_Id(request.getId());
            List<Map<String, Object>> clothMap = cloths.stream().map(cloth -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", cloth.getId());
                map.put("gender", cloth.getGender().getValue());
                map.put("clothCategory", cloth.getCategory().getValue());
                map.put("clothType", cloth.getType().getValue());
                map.put("color", cloth.getColor());
                map.put("logoImage", cloth.getLogoImage());
                map.put("note", cloth.getNote());
                map.put("gender", cloth.getGender().toLowerCase());

                List<FinalImage> finalImages = cloth.getFinalImages();

                if (finalImages != null && !finalImages.isEmpty()) {
                    List<Map<String, Object>> imageMap = finalImages.stream().map(
                            finalImage -> {
                                Map<String, Object> image = new HashMap<>();
                                image.put("name", finalImage.getName());
                                image.put("url", finalImage.getImageUrl());
                                return image;
                            }
                    ).toList();

                    map.put("finalImages", imageMap);
                } else {
                    map.put("finalImages", null);
                }
                return map;
            }).toList();

            requestMap.put("cloth", clothMap);
            return requestMap;
        }).toList();

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("List Design Complete")
                        .data(mapList)
                        .build()
        );
    }


    private void createSampleImageByCloth(Cloth cloth, List<CreateDesignRequest.Image> images) {
        for (CreateDesignRequest.Image image : images) {
            sampleImageRepo.save(
                    SampleImage.builder()
                            .imageUrl(image.getUrl())
                            .cloth(cloth)
                            .build());
        }
    }
}
