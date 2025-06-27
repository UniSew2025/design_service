package com.unisew.design_service.service;

import com.unisew.design_service.enums.Status;
import com.unisew.design_service.models.*;
import com.unisew.design_service.repositories.*;
import com.unisew.design_service.request.*;
import com.unisew.design_service.response.ResponseObject;
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
    final DesignDraftRepo designDraftRepo;
    final RevisionRequestRepo revisionRequestRepo;
    private final DesignCommentRepo designCommentRepo;
    private final DraftImageRepo draftImageRepo;

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
                    .gender(cloth.getGender())
                    .category(cloth.getCategory())
                    .logoImage(cloth.getLogoImage())
                    .logoPosition(cloth.getLogoPosition())
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

        //add function getPackage after have service

        DesignRequest designRequest = designRequestRepo.findById(request.getDesignRequestId()).orElse(null);

        if (designRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Can not find Design Request with " + request.getDesignRequestId())
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
        Optional<DesignDraft> optDraft = designDraftRepo.findByIdAndCloth_Id(request.getDesignDraftId(), request.getClothId());
        if (optDraft.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Design Draft does not belong to this cloth")
                            .build()
            );
        }
        DesignDraft draft = optDraft.get();

        if (designDraftRepo.existsByCloth_IdAndIsFinalTrue(request.getClothId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Cannot create revision request when one of them is final")
                            .build()
            );
        }

        RevisionRequest revisionRequest = RevisionRequest.builder()
                .designDraft(draft)
                .note(request.getNote())
                .build();
        revisionRequestRepo.save(revisionRequest);

        DesignComment sysComment = DesignComment.builder()
                .designRequest(optDraft.get().getCloth().getDesignRequest())
                .senderId(request.getSenderId())
                .senderRole(request.getSenderRole())
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
                        comment ->{
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
        List<DesignRequest> completeList = designRequestRepo.findAllByStatus(Status.COMPLETED);

        if (completeList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                    ResponseObject.builder()
                            .message("No Design Requests found")
                            .build()
            );
        }

        List<Map<String, Object>> mapList = completeList.stream().map(request -> {
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("id", request.getId());
            requestMap.put("package", request.getPackageId());
            requestMap.put("creationDate", request.getCreationDate());
            requestMap.put("status", request.getStatus());
            requestMap.put("private", request.isPrivate());
            requestMap.put("school", request.getSchoolId());

            List<Cloth> cloths = clothRepo.getAllByDesignRequest_Id(request.getId());
            List<Map<String, Object>> clothMap = cloths.stream().map(cloth -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", cloth.getId());
                map.put("logoHeight", cloth.getLogoHeight());
                map.put("logoWidth", cloth.getLogoWidth());
                map.put("templateId", cloth.getTemplate() != null ? cloth.getTemplate().getId() : 0);
                map.put("clothCategory", cloth.getCategory().getValue());
                map.put("clothType", cloth.getType().getValue());
                map.put("color", cloth.getColor());
                map.put("logoImage", cloth.getLogoImage());
                map.put("logo_position", cloth.getLogoPosition());
                map.put("note", cloth.getNote());
                map.put("gender", cloth.getGender().toLowerCase());

                DesignDraft designDraft = designDraftRepo.findByCloth_IdAndIsFinalTrue(cloth.getId());
                if (designDraft != null) {
                    Map<String, Object> draftMap = new HashMap<>();
                    draftMap.put("id", designDraft.getId());
                    draftMap.put("description", designDraft.getDescription());
                    draftMap.put("designDate", designDraft.getDesignDate());
                    draftMap.put("final", designDraft.isFinal());

                    List<DraftImage> draftImages = draftImageRepo.findAllByDesignDraft_Id(designDraft.getId());
                    List<Map<String, Object>> imageMap = draftImages.stream().map(image -> {
                        Map<String, Object> link = new HashMap<>();
                        link.put("id", image.getId());
                        link.put("url", image.getImageUrl());
                        link.put("imageName", image.getName());
                        return link;
                    }).toList();

                    draftMap.put("images", imageMap.isEmpty() ? null : imageMap);
                    map.put("draft", draftMap);
                } else{
                    map.put("draft", null);
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
