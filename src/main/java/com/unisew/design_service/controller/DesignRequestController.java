package com.unisew.design_service.controller;

import com.unisew.design_service.models.DesignRequest;
import com.unisew.design_service.request.*;
import com.unisew.design_service.response.ResponseObject;
import com.unisew.design_service.service.DesignDraftService;
import com.unisew.design_service.service.DesignRequestService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/api/v1/design-request")
public class DesignRequestController {

    final DesignRequestService designRequestService;
    final DesignDraftService designDraftService;

    @GetMapping("/list-request")
    public ResponseEntity<ResponseObject> listRequest() {
        return designRequestService.getAllDesignRequests();
    }

    @PostMapping("/")
    public ResponseEntity<ResponseObject> addRequest(@RequestBody CreateDesignRequest request) {
        return designRequestService.createDesignRequest(request);
    }

    @PostMapping("/request")
    public ResponseEntity<ResponseObject> getRequestById(@RequestBody GetDesignRequestById request) {
        return designRequestService.getDesignRequestById(request);
    }

    @PostMapping("/package")
    public ResponseEntity<ResponseObject> pickPackage(@RequestBody PickPackageRequest request) {
        return designRequestService.pickPackage(request);
    }

    @PostMapping("/public")
    public ResponseEntity<ResponseObject> makeDesignPublic(@RequestBody MakeDesignPublicRequest request) {
        return designRequestService.makeDesignPublic(request);
    }

    @PostMapping("/design-draft")
    public ResponseEntity<ResponseObject> addDesignDraft(@RequestBody CreateDesignDraftRequest request) {
        return designDraftService.createDesignDraft(request);
    }

    @PostMapping("/final")
    public  ResponseEntity<ResponseObject> makeDesignDraftFinal(@RequestBody SetDesignDraftFinalRequest request) {
        return designDraftService.setDesignDraftFinal(request);
    }
}
