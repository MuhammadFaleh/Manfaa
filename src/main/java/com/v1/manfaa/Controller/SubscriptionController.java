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

    @GetMapping("/get") // admin
    public ResponseEntity<?> getAllSubscriptions(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(subscriptionService.getAllSubscription());
    }

    @PostMapping("/monthly") // user
    public ResponseEntity<?> monthlySubscription(@AuthenticationPrincipal User user) {
        subscriptionService.monthlySubscription(user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Monthly subscription created"));
    }

    @PostMapping("/yearly") // user
    public ResponseEntity<?> yearlySubscription(@AuthenticationPrincipal User user) {
        subscriptionService.yearlySubscription(user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Yearly subscription created"));
    }

    @PutMapping("/cancel") // user
    public ResponseEntity<?> cancelSubscription(@AuthenticationPrincipal User user) {
        subscriptionService.cancelSubscription(user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Subscription cancelled successfully"));
    }
}
