package com.v1.manfaa.Controller;

import com.v1.manfaa.Api.ApiResponse;
import com.v1.manfaa.DTO.In.ServiceBidDTOIn;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Service.ServiceBidService;
import com.v1.manfaa.ValidationGroups.ValidationGroup1;
import com.v1.manfaa.ValidationGroups.ValidationGroup2;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/service-bid")
@RequiredArgsConstructor
public class ServiceBidController {

    private final ServiceBidService serviceBidService;

    @GetMapping("/get-all") // admin
    public ResponseEntity<?> getAllBids(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(serviceBidService.getAllBids());
    }

    @PostMapping("/create/{request_id}") // user
    public ResponseEntity<?> createBid(@PathVariable Integer request_id,
                                       @Validated(ValidationGroup1.class) @RequestBody ServiceBidDTOIn dto,
                                       @AuthenticationPrincipal User user) {
        serviceBidService.createBid(user.getId(), request_id, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Bid Created Successfully"));
    }

    @PutMapping("/update/{bid_id}") // user
    public ResponseEntity<?> updateBid(@PathVariable Integer bid_id,
                                       @Validated(ValidationGroup1.class) @RequestBody ServiceBidDTOIn dto,
                                       @AuthenticationPrincipal User user) {
        serviceBidService.updateBid(dto, user.getId(), bid_id);
        return ResponseEntity.status(200).body(new ApiResponse("Bid Updated Successfully"));
    }

    @DeleteMapping("/delete/{bid_id}") // user
    public ResponseEntity<?> deleteBid(@PathVariable Integer bid_id,
                                       @AuthenticationPrincipal User user) {
        serviceBidService.deleteBid(user.getId(), bid_id);
        return ResponseEntity.status(200).body(new ApiResponse("Bid Deleted Successfully"));
    }

    @PutMapping("/accept/{bid_id}") // user
    public ResponseEntity<?> acceptBid(@PathVariable Integer bid_id,
                                       @AuthenticationPrincipal User user) {
        serviceBidService.acceptServiceBid(bid_id, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Bid Accepted Successfully"));
    }

    @PutMapping("/reject/{bid_id}") // user
    public ResponseEntity<?> rejectBid(@PathVariable Integer bid_id,
                                       @Validated(ValidationGroup2.class) @RequestBody ServiceBidDTOIn dto,
                                       @AuthenticationPrincipal User user) {
        serviceBidService.rejectServiceBid(bid_id, user.getId(), dto.getNotes());
        return ResponseEntity.status(200).body(new ApiResponse("Bid Rejected Successfully"));
    }
}