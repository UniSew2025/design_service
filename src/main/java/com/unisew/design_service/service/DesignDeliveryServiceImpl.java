package com.unisew.design_service.service;

import com.unisew.design_service.enums.Status;
import com.unisew.design_service.models.*;
import com.unisew.design_service.repositories.*;
import com.unisew.design_service.request.AddFinalImagesRequest;
import com.unisew.design_service.request.SubmitDeliveryRequest;
import com.unisew.design_service.response.ResponseObject;
import com.unisew.design_service.utils.AccessCurrentLoginUser;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignDeliveryServiceImpl implements DesignDeliveryService {

    final DesignItemRepo designItemRepo;
    final DesignRequestRepo designRequestRepo;
    final FinalImageRepo finalImageRepo;
    final DesignCommentRepo designCommentRepo;
    final DesignDeliveryRepo designDeliveryRepo;
    private final RevisionRequestRepo revisionRequestRepo;
    final AccountService accountService;


    @Override
    public ResponseEntity<ResponseObject> submitDelivery(SubmitDeliveryRequest request) {

        DesignRequest designRequest = designRequestRepo.findById(request.getRequestId()).orElse(null);
        if (designRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .message("DesignRequest not exist")
                            .build());
        }
        if(designRequest.getStatus().equals(Status.COMPLETED)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .message("DesignRequest is already completed")
                            .build());
        }

        Optional<DesignDelivery> latestDelivery = designDeliveryRepo.findTopByDesignRequest_IdOrderByDeliveryNumberDesc(request.getRequestId());
        int nextDeliveryNumber = latestDelivery.map(d -> d.getDeliveryNumber() + 1).orElse(1);

        RevisionRequest revisionRequest = null;
        if (request.isRevision() && request.getRevisionId() != null && request.getRevisionId() != 0) {
            revisionRequest = revisionRequestRepo.findById(request.getRevisionId()).orElse(null);
        }

        DesignDelivery delivery = DesignDelivery.builder()
                .designRequest(designRequest)
                .fileUrl(request.getFileUrl())
                .deliveryNumber(nextDeliveryNumber)
                .submitDate(LocalDateTime.now())
                .parentRevision(revisionRequest)
                .designFinal(false)
                .revision(request.isRevision())
                .note(request.getNote())
                .build();
        designDeliveryRepo.save(delivery);

        DesignComment comment = DesignComment.builder()
                .designRequest(designRequest)
                .senderId(0)
                .senderRole("system")
                .content("Designer delivered a new version #" + nextDeliveryNumber)
                .creationDate(LocalDateTime.now())
                .build();
        designCommentRepo.save(comment);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("id", delivery.getId());
        responseMap.put("fileUrl", delivery.getFileUrl());
        responseMap.put("deliveryNumber", delivery.getDeliveryNumber());
        responseMap.put("submitDate", delivery.getSubmitDate());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .message("Submit delivery successfully. Delivery #" + nextDeliveryNumber)
                        .data(responseMap)
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> getAllDeliveryByRequestId(int requestId) {
        List<DesignDelivery> list = designDeliveryRepo.findAllByDesignRequest_Id(requestId);

        Integer accountId = AccessCurrentLoginUser.getId();

        String accessToken = null;
        if (accountId != null) {
            Map<String, Object> tokenResponse = accountService.getGoogleAccessToken(accountId);
            if (tokenResponse != null && tokenResponse.get("data") != null) {
                Map<String, Object> data = (Map<String, Object>) tokenResponse.get("data");
                accessToken = (String) data.get("access_token");
            } else if (tokenResponse != null && tokenResponse.get("access_token") != null) {
                accessToken = (String) tokenResponse.get("access_token");
            }
        }

        List<Map<String, Object>> listMap = list.stream().map(
                designDelivery -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", designDelivery.getId());
                    map.put("submitDate", designDelivery.getSubmitDate());
                    map.put("fileUrl", designDelivery.getFileUrl());
                    map.put("note", designDelivery.getNote());
                    map.put("deliveryNumber", designDelivery.getDeliveryNumber());
                    map.put("isFinal", designDelivery.isDesignFinal());
                    map.put("isRevision", designDelivery.isRevision());

                    List<RevisionRequest> revisionRequestList = designDelivery.getRevisionRequests();
                    if(revisionRequestList != null && !revisionRequestList.isEmpty()){
                        List<Map<String, Object>> revisionMap = revisionRequestList.stream().map(
                                revisionRequest -> {
                                    Map<String, Object> revision = new HashMap<>();
                                    revision.put("id", revisionRequest.getId());
                                    revision.put("deliveryId", revisionRequest.getDelivery().getId());
                                    revision.put("createAt", revisionRequest.getRequestDate());
                                    revision.put("note", revisionRequest.getNote());
                                    return revision;
                                }
                        ).toList();
                        map.put("revision", revisionMap);
                    }
                    return map;
                }
        ).toList();

        Map<String, Object> data = new HashMap<>();
        data.put("deliveries", listMap);
        data.put("google_access_token", accessToken);

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("List Design delivery successfully")
                        .data(data)
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> getAllUnUsedRevisionRequest(int requestId) {

        List<DesignDelivery> designDeliveryList = designDeliveryRepo.findAllByDesignRequest_Id(requestId);

        List<RevisionRequest> revisionRequestList = revisionRequestRepo.findAllByDelivery_DesignRequest_Id(requestId);

        if (revisionRequestList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .message("No revision request for this request")
                            .build()
            );
        }

        Set<Integer> usedRevisionIds = designDeliveryList.stream()
                .map(DesignDelivery::getParentRevision)
                .filter(Objects::nonNull)
                .map(RevisionRequest::getId)
                .collect(Collectors.toSet());

        List<RevisionRequest> unusedRevisionRequests = revisionRequestList.stream()
                .filter(rev -> !usedRevisionIds.contains(rev.getId()))
                .toList();

        List<Map<String,Object>> mapList = unusedRevisionRequests.stream()
                .map(
                        revisionRequest -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("id", revisionRequest.getId());
                            map.put("deliveryId", revisionRequest.getDelivery().getId());
                            map.put("requestDate", revisionRequest.getRequestDate());
                            map.put("note", revisionRequest.getNote());
                            return map;
                        }
                ).toList();

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message("List revision request not completed")
                        .data(mapList)
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> makeDeliveryFinalAndRequestComplete(int deliveryId, int requestId) {

        DesignDelivery designDelivery = designDeliveryRepo.findById(deliveryId).orElse(null);

        DesignRequest designRequest = designRequestRepo.findById(requestId).orElse(null);

        if(designDelivery == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .message("No design delivery found")
                            .build()
            );
        }
        if(designRequest == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .message("No design requests found")
                            .build()
            );
        }

        if(designDelivery.isDesignFinal()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Design delivery already final")
                            .build()
            );
        }

        designDelivery.setDesignFinal(true);


        designDeliveryRepo.save(designDelivery);



        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Make final successfully")
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> AddFinalImages(AddFinalImagesRequest request) {

        DesignRequest designRequest = designRequestRepo.findById(request.getRequestId()).orElse(null);

        List<DesignItem> designItems = designItemRepo.getAllByDesignRequest_Id(request.getRequestId());

        if (designRequest == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .message("Cannot find design request")
                            .build()
            );
        }

        if (designItems.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .message("No design items found for this request")
                            .build()
            );
        }

        List<DesignDelivery> deliveries = designRequest.getDeliveries();
        if(deliveries.stream().noneMatch(DesignDelivery::isDesignFinal)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("No delivery final to add finalImage")
                            .build()
            );
        }

        Map<Integer, DesignItem> designItemMap = designItems.stream()
                .collect(Collectors.toMap(DesignItem::getId, d -> d));

        List<FinalImage> imagesToSave = new ArrayList<>();
        List<String> failedImages = new ArrayList<>();

        for (AddFinalImagesRequest.Image imageRequest : request.getImages()) {
            DesignItem item = designItemMap.get(imageRequest.getDesignItemId());
            if (item != null) {
                FinalImage image = FinalImage.builder()
                        .designItem(item)
                        .name(imageRequest.getImageName())
                        .imageUrl(imageRequest.getImageUrl())
                        .build();
                imagesToSave.add(image);
            } else {
                failedImages.add(String.valueOf(imageRequest.getDesignItemId()));
            }
        }

        if (!imagesToSave.isEmpty()) {
            finalImageRepo.saveAll(imagesToSave);
            designRequest.setStatus(Status.COMPLETED);
            designRequestRepo.save(designRequest);
        }

        String message;
        if (failedImages.isEmpty()) {
            message = "All images saved successfully.";
        } else if (!imagesToSave.isEmpty()) {
            message = "Some images saved, but not found design items: " + String.join(", ", failedImages);
        } else {
            message = "No images saved, design item IDs not found: " + String.join(", ", failedImages);
        }

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message(message)
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> getAllFinalImagesByRequestId(int requestId) {

        List<FinalImage> finalImageList = finalImageRepo.findAllByDesignItem_DesignRequest_Id(requestId);

        if (finalImageList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .message("Cannot find any image for this request")
                            .build()
            );
        }

        List<Map<String, Object>> mapList = finalImageList.stream().map(
                finalImage -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", finalImage.getId());
                    map.put("designItemId", finalImage.getDesignItem().getId());
                    map.put("imageName", finalImage.getName());
                    map.put("imageUrl", finalImage.getImageUrl());
                    return map;
                }
        ).toList();
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("List final Image ")
                        .data(mapList)
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> getPaymentUrl(String orderInfo, long amount, HttpServletRequest request) {

        String baseUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

        String version = "2.1.1";

        String comment = "pay";

        String currency = "VND";

        String local = "vn";

        String orderType = "topup";

        String tmpCode = "5TXKG00O";

        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now(vietnamZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String vnpCreateDate = now.format(formatter);

        ZonedDateTime expireDate = now.plusMinutes(15);
        String vnpExpireDate = expireDate.format(formatter);
        String ipAddress = request.getRemoteAddr();


        String xForwardedForHeader = request.getHeader("X-FORWARDED-FOR");
        if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty()) {

            ipAddress = xForwardedForHeader.split(",")[0].trim();
        }
        return null;


    }


}
