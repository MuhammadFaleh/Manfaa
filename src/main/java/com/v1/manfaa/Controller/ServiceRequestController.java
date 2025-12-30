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

    @GetMapping("/get-requests") // user and admin
    public ResponseEntity<?> getServiceRequests(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getServiceRequests());
    }

    @PostMapping("/create-token-request") // user
    public ResponseEntity<?> createTokenRequest(@Valid @RequestBody ServiceRequestDTOIn dto,
                                                @AuthenticationPrincipal User user) {
        serviceRequestService.createTokenRequest(dto, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Token Request Created Successfully"));
    }

    @PostMapping("/create-barter-request") // user
    public ResponseEntity<?> createBarterRequest(@Valid @RequestBody ServiceRequestDTOIn dto,
                                                 @AuthenticationPrincipal User user) {
        serviceRequestService.createBarterRequest(dto, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Barter Request Created Successfully"));
    }

    @PostMapping("/create-either-request") // user
    public ResponseEntity<?> createEitherRequest(@Valid @RequestBody ServiceRequestDTOIn dto,
                                                 @AuthenticationPrincipal User user) {
        serviceRequestService.createEitherRequest(dto, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Either Request Created Successfully"));
    }

    @PutMapping("/update/{request_id}") // user
    public ResponseEntity<?> updateRequest(@PathVariable Integer request_id,
                                           @Valid @RequestBody ServiceRequestDTOIn dto,
                                           @AuthenticationPrincipal User user) {
        serviceRequestService.updateRequest(dto, user.getId(), request_id);
        return ResponseEntity.status(200).body(new ApiResponse("Request Updated Successfully"));
    }

    @DeleteMapping("/delete/{request_id}") // user
    public ResponseEntity<?> deleteRequest(@PathVariable Integer request_id,
                                           @AuthenticationPrincipal User user) {
        serviceRequestService.deleteRequest(request_id, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Request Deleted Successfully"));
    }

    @GetMapping("/get-all-with-bids") // admin
    public ResponseEntity<?> getAllRequestWithBids(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getAllRequestWithBids());
    }

    @GetMapping("/get-with-bids/{request_id}") // user
    public ResponseEntity<?> getServiceRequestWithBid(@PathVariable Integer request_id,
                                                      @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getServiceRequestWithBid(request_id));
    }

    @GetMapping("/get-company-requests/{company_id}") // admin
    public ResponseEntity<?> getServiceRequestOfCompany(@PathVariable Integer company_id,
                                                        @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getServiceRequestOfCompany(company_id));
    }

    @GetMapping("/search") // user
    public ResponseEntity<?> searchServiceRequests(@RequestParam String keyword,
                                                   @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.searchServiceRequests(keyword));
    }

    @GetMapping("/get-by-category/{category_id}") // user
    public ResponseEntity<?> getServiceRequestsByCategory(@PathVariable Integer category_id,
                                                          @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getServiceRequestsByCategory(category_id));
    }

    @GetMapping("/get-by-exchange-type/{exchange_type}") // user
    public ResponseEntity<?> getServiceRequestsByExchangeType(@PathVariable String exchange_type,
                                                              @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getServiceRequestsByExchangeType(exchange_type));
    }

    @GetMapping("/get-by-date-range") // user
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

    @GetMapping("/get-company-closed")
    public ResponseEntity<?> getClosedServiceRequestOfCompany(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getClosedServiceRequestOfCompany(user.getId()));
    }

    @GetMapping("/get-company-cancelled")
    public ResponseEntity<?> getCancelledServiceRequestOfCompany(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getCancelledServiceRequestOfCompany(user.getId()));
    }

    @GetMapping("/get-with-bids-subscribers/{request_id}") // admin and company
    public ResponseEntity<?> getServiceRequestWithBidSubscribers(@PathVariable Integer request_id,
                                                                 @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getServiceRequestWithBidSubscribers(request_id));
    }

    @GetMapping("/get-requests-subscriber") // admin and company
    public ResponseEntity<?> getServiceRequestsSubscriber(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceRequestService.getServiceRequestsSubscriber());
    }
}
