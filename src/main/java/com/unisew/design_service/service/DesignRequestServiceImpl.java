package com.unisew.design_service.service;

import com.unisew.design_service.enums.Gender;
import com.unisew.design_service.enums.Status;
import com.unisew.design_service.models.*;
import com.unisew.design_service.repositories.*;
import com.unisew.design_service.request.*;
import com.unisew.design_service.response.ResponseObject;
import com.unisew.design_service.utils.AccessCurrentLoginUser;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.type.descriptor.java.ObjectJavaType;
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
    final DesignItemRepo designItemRepo;
    final SampleImageRepo sampleImageRepo;
    final DesignDeliveryRepo designDeliveryRepo;
    final RevisionRequestRepo revisionRequestRepo;
    final DesignCommentRepo designCommentRepo;
    final FinalImageRepo finalImageRepo;
    final ProfileService profileService;


    @Override
    public ResponseEntity<ResponseObject> getAllDesignRequests() {
        // Change id to name after have another service
        List<Map<String, Object>> designRequests = designRequestRepo.findAll().stream().map(
                request -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", request.getId());
                    response.put("school", request.getSchoolId());
                    response.put("private", request.isDesignPrivate());
                    response.put("package", request.getPackageId());
                    response.put("packageName", request.getPackageName());
                    response.put("packagePrice", request.getPackagePrice());
                    response.put("headerContent", request.getPackageHeaderContent());
                    response.put("revisionTime", request.getRevisionTime());
                    response.put("deliveryDate", request.getPackageDeliveryDate());
                    response.put("creationDate",request.getCreationDate());
                    response.put("feedback", request.getFeedbackId());
                    response.put("status", request.getStatus().getValue());

                    List<DesignItem> itemList = request.getDesignItems();

                    List<Map<String, Object>> mapList = itemList.stream().map(
                            item -> {
                                Map<String, Object> clothMap = new HashMap<>();
                                clothMap.put("id", item.getId());
                                clothMap.put("type", item.getType());
                                clothMap.put("category", item.getCategory());
                                clothMap.put("note", item.getNote());
                                return clothMap;
                            }
                    ).toList();
                    response.put("clothes", mapList);

                    int revisionCount = (int) request.getDeliveries().stream()
                            .filter(DesignDelivery::isRevision)
                            .count();
                    response.put("revisionCount", revisionCount);

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
    public ResponseEntity<ResponseObject> getDesignRequestById(int id) {

        List<DesignRequest> designRequests = designRequestRepo.findAll();

        Map<String, Object> requestMap = designRequests.stream()
                .filter(d -> d.getId() == id)
                .findFirst()
                .map(designRequest -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", designRequest.getId());
                    response.put("school", designRequest.getSchoolId());
                    response.put("private", designRequest.isDesignPrivate());
                    response.put("package", designRequest.getPackageId());
                    response.put("packageName", designRequest.getPackageName());
                    response.put("packagePrice", designRequest.getPackagePrice());
                    response.put("headerContent", designRequest.getPackageHeaderContent());
                    response.put("revisionTime", designRequest.getRevisionTime());
                    response.put("deliveryDate", designRequest.getPackageDeliveryDate());
                    response.put("creationDate", designRequest.getCreationDate());
                    response.put("feedback", designRequest.getFeedbackId());
                    response.put("status", designRequest.getStatus().getValue());

                    List<Map<String, Object>> mapList = designRequest.getDesignItems().stream().map(item -> {
                        Map<String, Object> clothMap = new HashMap<>();
                        clothMap.put("id", item.getId());
                        clothMap.put("type", item.getType());
                        clothMap.put("category", item.getCategory());
                        clothMap.put("note", item.getNote());
                        return clothMap;
                    }).toList();

                    response.put("clothes", mapList);

                    int revisionCount = (int) designRequest.getDeliveries().stream()
                            .filter(DesignDelivery::isRevision)
                            .count();
                    response.put("revisionCount", revisionCount);

                    return response;
                })
                .orElse(null);


        if (requestMap == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .message("Design Request with ID " + id + " not found.")
                            .build()
            );
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Get Design Request by ID")
                        .data(requestMap)
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> createDesignRequest(CreateDesignRequest request) {
        DesignRequest designRequest = DesignRequest.builder()
                .creationDate(LocalDate.now())
                .schoolId(request.getSchoolId())
                .designPrivate(true)
                .status(Status.CREATED)
                .build();
        designRequestRepo.save(designRequest);

        for (CreateDesignRequest.Cloth cloth : request.getClothes()) {
            DesignItem newDesignItem = designItemRepo.save(DesignItem.builder()
                    .type(cloth.getType())
                    .gender(Gender.valueOf(cloth.getGender()))
                    .category(cloth.getCategory())
                    .logoImage(cloth.getLogoImage())
                    .logoPosition(cloth.getLogoPosition())
                    .designRequest(designRequest)
                    .color(cloth.getColor())
                    .note(cloth.getNote())
                    .build());

            if (cloth.getDesignType().equals("TEMPLATE")) {

                DesignItem template = designItemRepo.findById(cloth.getTemplateId()).orElse(null);
                if (template == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            ResponseObject.builder()
                                    .message("Template Not Found")
                                    .build()
                    );
                }
                newDesignItem.setTemplate(template);
                designItemRepo.save(newDesignItem);
            }

            if (cloth.getDesignType().equals("UPLOAD")) {
                createSampleImageByCloth(newDesignItem, cloth.getImages());
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
        designRequest.setPackageName(request.getPackageName());
        designRequest.setRevisionTime(request.getRevisionTime());
        designRequest.setPackageDeliveryDate(request.getPackageDeliveryDate());
        designRequest.setPackageHeaderContent(request.getPackageHeaderContent());
        designRequest.setPackagePrice(request.getGetPackagePrice());
        designRequest.setStatus(Status.PAID);
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

        if(!designRequest.isDesignPrivate()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("design already public")
                            .build()
            );
        }

        designRequest.setDesignPrivate(false);
        designRequestRepo.save(designRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Change design to public successfully")
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> createRevisionDesign(CreateRevisionDesignRequest request) {

        Optional<DesignDelivery> optDelivery = designDeliveryRepo.findById(request.getDeliveryId());
        if (optDelivery.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("DesignDelivery not found")
                            .build()
            );
        }
        DesignDelivery delivery = optDelivery.get();

        if (delivery.isDesignFinal()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Cannot create revision: Delivery has been finalized.")
                            .build()
            );
        }

        RevisionRequest revisionRequest = RevisionRequest.builder()
                .delivery(delivery)
                .note(request.getNote())
                .requestDate(LocalDate.now())
                .build();
        revisionRequestRepo.save(revisionRequest);

        DesignComment sysComment = DesignComment.builder()
                .designRequest(delivery.getDesignRequest())
                .senderId(0)
                .senderRole("system")
                .content("School requested a revision for delivery " + request.getDeliveryId())
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

        Integer accountId = AccessCurrentLoginUser.getId();

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

            DesignDelivery delivery = designDeliveryRepo.findByDesignRequest_IdAndDesignFinalTrue(request.getId());

            requestMap.put("deliveryUrl", delivery == null ? null : delivery.getFileUrl());

            List<DesignItem> designItems = designItemRepo.getAllByDesignRequest_Id(request.getId());
            List<Map<String, Object>> clothMap = designItems.stream().map(cloth -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", cloth.getId());
                map.put("gender", cloth.getGender().getValue());
                map.put("clothCategory", cloth.getCategory().getValue());
                map.put("clothType", cloth.getType().getValue());
                map.put("color", cloth.getColor());
                map.put("logoImage", cloth.getLogoImage());
                map.put("note", cloth.getNote());

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

    @Override
    public ResponseEntity<ResponseObject> sendComment(SendCommentRequest request) {

        DesignRequest designRequest = designRequestRepo.findById(request.getRequestId()).orElse(null);
        if (designRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Can not find design request")
                            .build()
            );
        }

        if(designRequest.getDeliveries().stream().anyMatch(DesignDelivery::isDesignFinal)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Can not send comment because it is final")
                            .build()
            );
        }



        Integer senderId = AccessCurrentLoginUser.getId();
        String senderRole = AccessCurrentLoginUser.getRole();

        DesignComment newComment = DesignComment
                .builder()
                .designRequest(designRequest)
                .senderId(senderId)
                .senderRole(senderRole)
                .creationDate(LocalDateTime.now())
                .content(request.getComment())
                .build();

        designCommentRepo.save(newComment);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .message("Comment sent successfully")
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> getAllDesignByPackageId(GetAllDesignByPackageIdRequest request) {

        //can 2 ham internal 1 getProfile, 1 package tu profile_service


//        Map<String, Object> response = profileService.getProfileInfo(accountId);
//
//        Map<String, Object> designer = (Map<String, Object>) response.get("designer");
//        String designerName = (String) designer.get("name");
        List<DesignRequest> list = designRequestRepo.findAllByPackageIdIn(request.getPackageIds());

        if (list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .message("No Design Requests found")
                            .build()
            );
        }
        List<Map<String, Object>> mapList = list.stream().map(designRequest -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", designRequest.getId());
            map.put("packageId", designRequest.getPackageId()); // -> thay bang package name
            map.put("creationDate", designRequest.getCreationDate());
            map.put("packageName", designRequest.getPackageName());
            map.put("packagePrice", designRequest.getPackagePrice());
            map.put("headerContent", designRequest.getPackageHeaderContent());
            map.put("revisionTime", designRequest.getRevisionTime());
            map.put("deliveryDate", designRequest.getPackageDeliveryDate());
            map.put("status", designRequest.getStatus());
            map.put("private", designRequest.isDesignPrivate());
            map.put("school", profileService.getProfileInfo(designRequest.getSchoolId())); // -> thay bang school name
            return map;
        }).toList();

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("List DesignRequest by packageIds")
                        .data(mapList)
                        .build()
        );
    }



    private void createSampleImageByCloth(DesignItem designItem, List<CreateDesignRequest.Image> images) {
        for (CreateDesignRequest.Image image : images) {
            sampleImageRepo.save(
                    SampleImage.builder()
                            .imageUrl(image.getUrl())
                            .designItem(designItem)
                            .build());
        }
    }

    //-----------------------------Internal Method-----------------------------
    @Override
    public boolean isSafeToBan(List<Integer> packageIds) {
        List<DesignRequest> list = designRequestRepo.findAllByPackageIdIn(packageIds);
        for (DesignRequest designRequest : list) {
            if (designRequest.getStatus() != Status.COMPLETED) {
                return false;
            }
        }
        return true;
    }

}
