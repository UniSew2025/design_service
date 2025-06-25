package com.unisew.design_service.controller;

import com.unisew.design_service.models.DesignRequest;
import com.unisew.design_service.request.*;
import com.unisew.design_service.response.ResponseObject;
import com.unisew.design_service.service.ClothService;
import com.unisew.design_service.service.DesignDraftService;
import com.unisew.design_service.service.DesignRequestService;
import com.unisew.design_service.service.SampleImageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/api/v1/design")
public class DesignRequestController {

    final DesignRequestService designRequestService;
    final DesignDraftService designDraftService;
    final ClothService clothService;
    final SampleImageService sampleImageService;

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
    @PostMapping("/revision")
    public ResponseEntity<ResponseObject> createRevisionRequest(@RequestBody CreateRevisionDesignRequest request) {
        return designRequestService.createRevisionDesign(request);
    }

    @PostMapping("/cloth-list")
    public ResponseEntity<ResponseObject> getListClothByRequestId(@RequestBody GetAllClothByRequestId request) {
        return clothService.getAllClothesByRequestId(request);
    }
    @GetMapping("/sampleImage-list")
    public ResponseEntity<ResponseObject> getListSampleImages(){
        return sampleImageService.getAllSampleImages();
    }

    @GetMapping("/design-request/{id}/comments")
    public ResponseEntity<ResponseObject> getListComments(@PathVariable int id) {
        return designRequestService.getAllDesignComments(id);
    }
    @GetMapping("/complete-list")
    public ResponseEntity<ResponseObject> getListComplete() {
        return designRequestService.getListDesignComplete();
    }

}
