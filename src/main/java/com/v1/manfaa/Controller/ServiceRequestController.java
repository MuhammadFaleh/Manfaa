package com.v1.manfaa.Controller;

import com.v1.manfaa.Api.ApiResponse;
import com.v1.manfaa.DTO.In.ServiceRequestDTOIn;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Service.ServiceRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/service-request")
@RequiredArgsConstructor
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    @GetMapping("/get-requests")
    public ResponseEntity<?> getServiceRequests(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getServiceRequests());
    }

    @PostMapping("/create-token-request")
    public ResponseEntity<?> createTokenRequest(@Valid @RequestBody ServiceRequestDTOIn dto,
                                                @AuthenticationPrincipal User user) {
        serviceRequestService.createTokenRequest(dto, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Token Request Created Successfully"));
    }

    @PostMapping("/create-barter-request")
    public ResponseEntity<?> createBarterRequest(@Valid @RequestBody ServiceRequestDTOIn dto,
                                                 @AuthenticationPrincipal User user) {
        serviceRequestService.createBarterRequest(dto, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Barter Request Created Successfully"));
    }

    @PostMapping("/create-either-request")
    public ResponseEntity<?> createEitherRequest(@Valid @RequestBody ServiceRequestDTOIn dto,
                                                 @AuthenticationPrincipal User user) {
        serviceRequestService.createEitherRequest(dto, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Either Request Created Successfully"));
    }

    @PutMapping("/update/{request_id}")
    public ResponseEntity<?> updateRequest(@PathVariable Integer request_id,
                                           @Valid @RequestBody ServiceRequestDTOIn dto,
                                           @AuthenticationPrincipal User user) {
        serviceRequestService.updateRequest(dto, user.getId(), request_id);
        return ResponseEntity.status(200).body(new ApiResponse("Request Updated Successfully"));
    }

    @DeleteMapping("/delete/{request_id}")
    public ResponseEntity<?> deleteRequest(@PathVariable Integer request_id,
                                           @AuthenticationPrincipal User user) {
        serviceRequestService.deleteRequest(request_id, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Request Deleted Successfully"));
    }

    @GetMapping("/get-all-with-bids")
    public ResponseEntity<?> getAllRequestWithBids(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getAllRequestWithBids());
    }

    @GetMapping("/get-with-bids/{request_id}")
    public ResponseEntity<?> getServiceRequestWithBid(@PathVariable Integer request_id,
                                                      @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getServiceRequestWithBid(request_id));
    }

    @GetMapping("/get-company-requests/{company_id}")
    public ResponseEntity<?> getServiceRequestOfCompany(@PathVariable Integer company_id,
                                                        @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getServiceRequestOfCompany(company_id));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchServiceRequests(@RequestParam String keyword,
                                                   @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.searchServiceRequests(keyword));
    }

    @GetMapping("/get-by-category/{category_id}")
    public ResponseEntity<?> getServiceRequestsByCategory(@PathVariable Integer category_id,
                                                          @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getServiceRequestsByCategory(category_id));
    }

    @GetMapping("/get-by-exchange-type/{exchange_type}")
    public ResponseEntity<?> getServiceRequestsByExchangeType(@PathVariable String exchange_type,
                                                              @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getServiceRequestsByExchangeType(exchange_type));
    }

    @GetMapping("/get-by-date-range")
    public ResponseEntity<?> getServiceRequestsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getServiceRequestsByDateRange(startDate, endDate));
    }

    @GetMapping("/get-by-token-range")
    public ResponseEntity<?> getServiceRequestsByTokenRange(@RequestParam Double minAmount,
                                                            @RequestParam Double maxAmount,
                                                            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getServiceRequestsByTokenRange(minAmount, maxAmount));
    }

    @GetMapping("/get-sorted-by-token")
    public ResponseEntity<?> getServiceRequestsSortedByTokenAmount(@RequestParam String order,
                                                                   @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getServiceRequestsSortedByTokenAmount(order));
    }

    @GetMapping("/get-company-open/{company_id}")
    public ResponseEntity<?> getOpenServiceRequestOfCompany(@PathVariable Integer company_id,
                                                            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getOpenServiceRequestOfCompany(company_id));
    }

    @GetMapping("/get-company-closed/{company_id}")
    public ResponseEntity<?> getClosedServiceRequestOfCompany(@PathVariable Integer company_id,
                                                              @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getClosedServiceRequestOfCompany(company_id));
    }

    @GetMapping("/get-company-cancelled/{company_id}")
    public ResponseEntity<?> getCancelledServiceRequestOfCompany(@PathVariable Integer company_id,
                                                                 @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getCancelledServiceRequestOfCompany(company_id));
    }
}
