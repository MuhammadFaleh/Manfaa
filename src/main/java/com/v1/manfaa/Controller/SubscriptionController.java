package com.v1.manfaa.Controller;

import com.v1.manfaa.Api.ApiResponse;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllSubscriptions() {
        return ResponseEntity.status(200).body(subscriptionService.getAllSubscription());
    }

    @PostMapping("/monthly/{companyProfileId}")
    public ResponseEntity<ApiResponse> monthlySubscription(@PathVariable Integer companyProfileId) {
        subscriptionService.monthlySubscription(companyProfileId);
        return ResponseEntity.status(200).body(new ApiResponse("Monthly subscription created"));
    }

    @PostMapping("/yearly/{companyProfileId}")
    public ResponseEntity<ApiResponse> yearlySubscription(@PathVariable Integer companyProfileId) {
        subscriptionService.yearlySubscription(companyProfileId);
        return ResponseEntity.status(200).body(new ApiResponse("Yearly subscription created"));
    }

    @PutMapping("/cancel/{subscriptionId}")
    public ResponseEntity cancelSubscription(@AuthenticationPrincipal User user ,  @PathVariable Integer subscriptionId) {
        subscriptionService.cancelSubscription(user.getId(),subscriptionId);
        return ResponseEntity.status(200).body("Subscription cancelled successfully");
    }
}
