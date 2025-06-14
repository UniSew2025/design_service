package com.unisew.design_service.controller;

import com.unisew.design_service.models.DesignRequest;
import com.unisew.design_service.request.CreateDesignRequest;
import com.unisew.design_service.request.GetDesignRequestById;
import com.unisew.design_service.response.ResponseObject;
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
}
