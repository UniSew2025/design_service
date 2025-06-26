package com.unisew.design_service.service;

import com.unisew.design_service.models.Cloth;
import com.unisew.design_service.models.DesignComment;
import com.unisew.design_service.models.DesignDraft;
import com.unisew.design_service.models.DraftImage;
import com.unisew.design_service.repositories.ClothRepo;
import com.unisew.design_service.repositories.DesignCommentRepo;
import com.unisew.design_service.repositories.DesignDraftRepo;
import com.unisew.design_service.repositories.DraftImageRepo;
import com.unisew.design_service.request.CreateDesignDraftRequest;
import com.unisew.design_service.request.SetDesignDraftFinalRequest;
import com.unisew.design_service.request.SubmitDeliveryRequest;
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
public class DesignDraftServiceImpl implements DesignDraftService {

    final ClothRepo clothRepo;
    final DesignDraftRepo designDraftRepo;
    final DraftImageRepo draftImageRepo;
    final DesignCommentRepo designCommentRepo;

    @Override
    public ResponseEntity<ResponseObject> createDesignDraft(CreateDesignDraftRequest request) {

        Cloth cloth = clothRepo.findById(request.getClothId()).orElse(null);


        if (cloth == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Can not find cloth with id " + request.getClothId())
                            .build()
            );
        }

        DesignDraft designDraft = DesignDraft.builder()
                .cloth(cloth)
                .description(request.getDescriptions())
                .designDate(LocalDate.now())
                .isFinal(false)
                .build();
        designDraftRepo.save(designDraft);

        List<DraftImage> draftImages = request.getImages().stream()
                .map(image -> DraftImage.builder()
                        .imageUrl(image.getUrl())
                        .name(image.getName())
                        .designDraft(designDraft)
                        .build()
                ).toList();
        draftImageRepo.saveAll(draftImages);

        designDraft.setDraftImages(draftImages);
        designDraftRepo.save(designDraft);

        DesignComment systemComment = DesignComment.builder()
                .designRequest(cloth.getDesignRequest())
                .senderId(0)
                .senderRole("system")
                .content("Designer delivered a new design version " )
                .creationDate(LocalDateTime.now())
                .build();
        designCommentRepo.save(systemComment);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .message("Upload Design draft successful")
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> setDesignDraftFinal(SetDesignDraftFinalRequest request) {

        DesignDraft designDraft = designDraftRepo.findById(request.getDesignDraftId()).orElse(null);

        if (designDraft == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Can not find design draft with id " + request.getDesignDraftId())
                            .build()
            );
        }

            designDraft.setFinal(true);
            designDraftRepo.save(designDraft);

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Make design draft final successful")
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> submitDelivery(SubmitDeliveryRequest request) {

        List<Integer> clothIds = request.getDrafts().stream()
                .map(SubmitDeliveryRequest.DraftItem::getClothId)
                .toList();

        List<Cloth> cloths = clothRepo.findAllById(clothIds);

        if(clothIds.size() != cloths.size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("One or more cloth are not exist")
                            .build()
            );
        }

        Integer firstRequestId = cloths.get(0).getDesignRequest().getId();
        boolean sameRequest = cloths.stream()
                .allMatch(cloth -> cloth.getDesignRequest().getId().equals(firstRequestId));

        if(!sameRequest || !firstRequestId.equals(request.getRequestId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("All cloth must in one request")
                            .build()
            );
        }

        List<DesignDraft> existDesignDrafts = designDraftRepo.findAllByCloth_IdIn(clothIds);

        int nextDeliveryNumber = existDesignDrafts.stream()
                .mapToInt(draft -> draft.getDeliveryNumber() == null ?0 : draft.getDeliveryNumber())
                .max()
                .orElse(0) + 1;


        List<DesignDraft> newDrafts = new ArrayList<>();

        for (SubmitDeliveryRequest.DraftItem draftReq : request.getDrafts()) {
            Cloth cloth = clothRepo.findById(draftReq.getClothId()).orElse(null);
            if (cloth == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ResponseObject.builder()
                                .message("Can not find cloth with id " + draftReq.getClothId())
                                .build()
                );
            }

            DesignDraft designDraft = DesignDraft.builder()
                    .cloth(cloth)
                    .description(draftReq.getDescriptions())
                    .designDate(LocalDate.now())
                    .isFinal(false)
                    .deliveryNumber(nextDeliveryNumber)
                    .build();
            designDraftRepo.save(designDraft);

            List<DraftImage> draftImages = new ArrayList<>(
                    draftReq.getImages().stream()
                            .map(image -> DraftImage.builder()
                                    .imageUrl(image.getUrl())
                                    .name(image.getName())
                                    .designDraft(designDraft)
                                    .build())
                            .toList()
            );
            draftImageRepo.saveAll(draftImages);

            designDraft.setDraftImages(draftImages);
            designDraftRepo.save(designDraft);

            newDrafts.add(designDraft);

        }

        Cloth firstCloth = clothRepo.findById(request.getDrafts().get(0).getClothId()).orElse(null);
        if (firstCloth != null) {
            DesignComment systemComment = DesignComment.builder()
                    .designRequest(firstCloth.getDesignRequest())
                    .senderId(0)
                    .senderRole("system")
                    .content("Designer delivered a new design version #" + nextDeliveryNumber)
                    .creationDate(LocalDateTime.now())
                    .build();
            designCommentRepo.save(systemComment);
        }
        List<Map<String, Object>> responseMap = newDrafts.stream().map(draft -> {
            Map<String, Object> draftMap = new HashMap<>();
            draftMap.put("id", draft.getId());
            draftMap.put("description", draft.getDescription());
            draftMap.put("designDate", draft.getDesignDate());
            draftMap.put("deliveryNumber", draft.getDeliveryNumber());

            Cloth cloth = draft.getCloth();
            Map<String, Object> clothMap = new HashMap<>();
            clothMap.put("id", cloth.getId());
            clothMap.put("type", cloth.getType().getValue());
            clothMap.put("category", cloth.getCategory().getValue());
            clothMap.put("color", cloth.getColor());
            clothMap.put("fabric", cloth.getFabric());
            clothMap.put("note", cloth.getNote());
            clothMap.put("logoImage", cloth.getLogoImage());

            draftMap.put("cloth", clothMap);

            List<Map<String, Object>> imageList = draft.getDraftImages().stream().map(image -> {
                Map<String, Object> imageMap = new HashMap<>();
                imageMap.put("url", image.getImageUrl());
                imageMap.put("name", image.getName());
                return imageMap;
            }).toList();

            draftMap.put("images", imageList);

            return draftMap;
        }).toList();


        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .message("Submit Delivery thành công. Delivery #" + nextDeliveryNumber)
                        .data(responseMap)
                        .build()
        );
    }
}
