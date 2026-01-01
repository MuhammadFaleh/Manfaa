package com.v1.manfaa.Controller;

import com.v1.manfaa.Api.ApiResponse;
import com.v1.manfaa.DTO.In.ReviewDTOIn;
import com.v1.manfaa.DTO.Out.ReviewDTOOut;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    @GetMapping("/get-all") // admin
    public ResponseEntity<List<ReviewDTOOut>> getAllReviews(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(reviewService.getAllReviews(user.getId()));
    }


    @PostMapping("/add/{reviewedCompanyId}/{contractId}") // user
    public ResponseEntity<ApiResponse> addReview(@PathVariable Integer reviewedCompanyId, @PathVariable Integer contractId,
                                                 @Valid @RequestBody ReviewDTOIn reviewDTOIn, @AuthenticationPrincipal User user) {
        reviewService.addReview(user.getId(), reviewedCompanyId, contractId, reviewDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Review added successfully"));
    }

    @PutMapping("/update/{reviewId}") // user
    public ResponseEntity<ApiResponse> updateReview(@PathVariable Integer reviewId, @Valid @RequestBody ReviewDTOIn reviewDTOIn, @AuthenticationPrincipal User user) {
        reviewService.updateReview(user.getId(), reviewId, reviewDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Review updated successfully"));
    }

    @DeleteMapping("/delete/{reviewId}") // user and admin
    public ResponseEntity<ApiResponse> deleteReview(@PathVariable Integer reviewId, @AuthenticationPrincipal User user) {
        reviewService.deleteReview(user.getId(), reviewId);
        return ResponseEntity.status(200).body(new ApiResponse("Review deleted successfully"));
    }


    @GetMapping("/get/{reviewId}") // user
    public ResponseEntity<ReviewDTOOut> getReviewById(@PathVariable Integer reviewId, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(reviewService.getReviewById(reviewId, user.getId()));
    }

    @GetMapping("/company/received") // user
    public ResponseEntity<List<ReviewDTOOut>> getReceivedReviewsByCompany( @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(reviewService.getReceivedReviewsByCompany(user.getId()));
    }

    @GetMapping("/company/written") // user
    public ResponseEntity<List<ReviewDTOOut>> getWrittenReviewsByCompany(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(reviewService.getWrittenReviewsByCompany(user.getId()));
    }

    @GetMapping("/company/reviewed-contracts") // user
    public ResponseEntity<List<ReviewDTOOut>> getReviewedContracts( @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(reviewService.getReviewedContracts(user.getId()));
    }


    @GetMapping("/search/{keyword}") // admin
    public ResponseEntity<List<ReviewDTOOut>> searchReviewsByKeyword(@PathVariable String keyword, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(reviewService.searchReviewsByKeyword(keyword));
    }

    @GetMapping("/exchange-type/{exchangeType}") // admin
    public ResponseEntity<List<ReviewDTOOut>> getReviewsByExchangeType(@PathVariable String exchangeType, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(reviewService.getReviewsByExchangeType(exchangeType));
    }


    @GetMapping("/company/best-to-worst") //user
    public ResponseEntity<ArrayList<ReviewDTOOut>> getBestToWorstReviews(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(reviewService.getBestToWorstReviews(user.getId()));
    }

    @PostMapping("/create-pending-reviews/{contractAgreementId}")
    public ResponseEntity<?> createPendingReviewsForContract(@PathVariable Integer contractAgreementId, @AuthenticationPrincipal User user) {
        reviewService.createPendingReviewsOnCompletion(contractAgreementId);
        return ResponseEntity.ok(new ApiResponse("Pending reviews created successfully for contract #" + contractAgreementId));
    }


    @PostMapping("/handle-rejection/{contractAgreementId}")
    public ResponseEntity<?> handleContractRejection(@PathVariable Integer contractAgreementId, @AuthenticationPrincipal User user) {
        reviewService.handleRejectionAndRefund(contractAgreementId);
        return ResponseEntity.ok(new ApiResponse("Contract rejection handled and tokens refunded for contract #" + contractAgreementId));
    }
}

