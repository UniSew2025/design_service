package com.unisew.design_service.controller;

import com.unisew.design_service.request.*;
import com.unisew.design_service.response.ResponseObject;
import com.unisew.design_service.service.ClothService;
import com.unisew.design_service.service.DesignDeliveryService;
import com.unisew.design_service.service.DesignRequestService;
import com.unisew.design_service.service.SampleImageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/api/v1/design")
public class DesignRequestController {

    final DesignRequestService designRequestService;
    final DesignDeliveryService designDeliveryService;
    final ClothService clothService;
    final SampleImageService sampleImageService;

    @GetMapping("/list-request")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> listRequest() {
        return designRequestService.getAllDesignRequests();
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> addRequest(@RequestBody CreateDesignRequest request) {
        return designRequestService.createDesignRequest(request);
    }

    @PostMapping("/request")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getRequestById(@RequestBody GetDesignRequestById request) {
        return designRequestService.getDesignRequestById(request);
    }

    @PostMapping("/package")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> pickPackage(@RequestBody PickPackageRequest request) {
        return designRequestService.pickPackage(request);
    }

    @PostMapping("/public")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> makeDesignPublic(@RequestBody MakeDesignPublicRequest request) {
        return designRequestService.makeDesignPublic(request);
    }

    @PostMapping("/revision")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> createRevisionRequest(@RequestBody CreateRevisionDesignRequest request) {
        return designRequestService.createRevisionDesign(request);
    }

    @PostMapping("/cloth-list")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getListClothByRequestId(@RequestBody GetAllClothByRequestId request) {
        return clothService.getAllClothesByRequestId(request);
    }
    @GetMapping("/sampleImage-list")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> getListSampleImages(){
        return sampleImageService.getAllSampleImages();
    }

    @GetMapping("/design-request/{id}/comments")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getListComments(@PathVariable int id) {
        return designRequestService.getAllDesignComments(id);
    }
    @GetMapping("/complete-list")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> getListComplete() {
        return designRequestService.getListDesignComplete();
    }

    @PostMapping("/deliveries")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> submitDelivery(@RequestBody SubmitDeliveryRequest request) {
        return designDeliveryService.submitDelivery(request);
    }

    @PostMapping("/comment")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> sendComment(@RequestBody SendCommentRequest request){
        return designRequestService.sendComment(request);
    }

    @PostMapping("/design-request/list-packageId")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getAllDesignByPackageId(@RequestBody GetAllDesignByPackageIdRequest request) {
        return designRequestService.getAllDesignByPackageId(request);
    }

    @GetMapping("/list-delivery/{id}")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getAllDeliveryByRequestId(@PathVariable("id") int id) {
        return designRequestService.getAllDeliveryByRequestId(id);
    }

}
