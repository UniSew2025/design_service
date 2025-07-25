package com.unisew.design_service.controller;

import com.unisew.design_service.request.*;
import com.unisew.design_service.response.ResponseObject;
import com.unisew.design_service.service.ClothService;
import com.unisew.design_service.service.DesignDeliveryService;
import com.unisew.design_service.service.DesignRequestService;
import com.unisew.design_service.service.SampleImageService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/request/{id}")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getRequestById(@PathVariable int id) {
        return designRequestService.getDesignRequestById(id);
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
        return designDeliveryService.getAllDeliveryByRequestId(id);
    }

    @GetMapping("/list-revision/{id}")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getAllUnUsedRevisionRequest(@PathVariable("id") int id) {
        return designDeliveryService.getAllUnUsedRevisionRequest(id);
    }

    @PostMapping("/makeFinal/{deliveryId}/{requestId}")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> makeFinal(@PathVariable int deliveryId, @PathVariable int requestId) {
        return designDeliveryService.makeDeliveryFinalAndRequestComplete(deliveryId, requestId);
    }

    @PostMapping("/final-image")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> AddFinalImages(@RequestBody AddFinalImagesRequest request) {
        return designDeliveryService.AddFinalImages(request);
    }

    @GetMapping("/list/final-image/{id}")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getAllFinalImages(@PathVariable int id) {
        return designDeliveryService.getAllFinalImagesByRequestId(id);
    }

    @GetMapping("/payment")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> getPaymentUrl(@RequestParam String orderInfo, @RequestParam long amount, HttpServletRequest request) {
        return designDeliveryService.getPaymentUrl(orderInfo, amount, request);
    }
}
