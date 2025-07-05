package com.unisew.design_service.service;

import com.unisew.design_service.enums.Status;
import com.unisew.design_service.models.Cloth;
import com.unisew.design_service.models.DesignComment;
import com.unisew.design_service.models.DesignDelivery;
import com.unisew.design_service.models.DesignRequest;
import com.unisew.design_service.models.FinalImage;
import com.unisew.design_service.repositories.ClothRepo;
import com.unisew.design_service.repositories.DesignCommentRepo;
import com.unisew.design_service.repositories.DesignDeliveryRepo;
import com.unisew.design_service.repositories.DesignRequestRepo;
import com.unisew.design_service.repositories.FinalImageRepo;
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
public class DesignDeliveryServiceImpl implements DesignDeliveryService {

    final ClothRepo clothRepo;
    final DesignRequestRepo designRequestRepo;
    final FinalImageRepo finalImageRepo;
    final DesignCommentRepo designCommentRepo;
    final DesignDeliveryRepo designDeliveryRepo;


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

        DesignDelivery delivery = DesignDelivery.builder()
                .designRequest(designRequest)
                .fileUrl(request.getFileUrl())
                .deliveryNumber(nextDeliveryNumber)
                .submitDate(LocalDateTime.now())
                .isFinal(false)
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
