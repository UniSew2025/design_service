package com.unisew.design_service.service;

import com.unisew.design_service.enums.Status;
import com.unisew.design_service.models.*;
import com.unisew.design_service.repositories.*;
import com.unisew.design_service.request.SubmitDeliveryRequest;
import com.unisew.design_service.response.ResponseObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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

        RevisionRequest revisionRequest = revisionRequestRepo.findById(request.getRequestId()).orElse(null);

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

}
