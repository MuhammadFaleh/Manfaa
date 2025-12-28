package com.v1.manfaa.Controller;

import com.v1.manfaa.Api.ApiResponse;
import com.v1.manfaa.DTO.In.ServiceRequestDTOIn;
import com.v1.manfaa.Model.ServiceRequest;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Service.ServiceRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/service-request")
@RequiredArgsConstructor
public class ServiceRequestController {
    private final ServiceRequestService serviceRequestService;

    @GetMapping("/get-requests")
    public ResponseEntity<?> getRequests(Authentication authentication){
        return ResponseEntity.status(200).body(serviceRequestService.getServiceRequests());
    }


    @PostMapping("/create-request")
    public ResponseEntity<?> createRequests(@Valid @RequestBody ServiceRequestDTOIn dtoIn, @AuthenticationPrincipal User user){
        serviceRequestService.createTokenRequest(dtoIn,user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("request created successfully"));
    }

}
