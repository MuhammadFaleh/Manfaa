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


    @GetMapping("/get-all")
    public ResponseEntity<List<ReviewDTOOut>> getAllReviews(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(reviewService.getAllReviews(user.getId()));
    }


    @PostMapping("/add/{reviewedCompanyId}/{contractId}")
    public ResponseEntity<ApiResponse> addReview(@PathVariable Integer reviewedCompanyId, @PathVariable Integer contractId, @Valid @RequestBody ReviewDTOIn reviewDTOIn, @AuthenticationPrincipal User user) {
        reviewService.addReview(user.getId(), reviewedCompanyId, contractId, reviewDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Review added successfully"));
    }

    @PutMapping("/update/{reviewId}")
    public ResponseEntity<ApiResponse> updateReview(@PathVariable Integer reviewId, @Valid @RequestBody ReviewDTOIn reviewDTOIn, @AuthenticationPrincipal User user) {
        reviewService.updateReview(user.getId(), reviewId, reviewDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Review updated successfully"));
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<ApiResponse> deleteReview(@PathVariable Integer reviewId, @AuthenticationPrincipal User user) {
        reviewService.deleteReview(user.getId(), reviewId);
        return ResponseEntity.status(200).body(new ApiResponse("Review deleted successfully"));
    }


    @GetMapping("/get/{reviewId}")
    public ResponseEntity<ReviewDTOOut> getReviewById(@PathVariable Integer reviewId, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(reviewService.getReviewById(reviewId, user.getId()));
    }

    @GetMapping("/company/{companyId}/received")
    public ResponseEntity<List<ReviewDTOOut>> getReceivedReviewsByCompany(@PathVariable Integer companyId, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(reviewService.getReceivedReviewsByCompany(companyId));
    }

    @GetMapping("/company/{companyId}/written")
    public ResponseEntity<List<ReviewDTOOut>> getWrittenReviewsByCompany(@PathVariable Integer companyId, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(reviewService.getWrittenReviewsByCompany(companyId));
    }

    @GetMapping("/company/{companyId}/reviewed-contracts")
    public ResponseEntity<List<ReviewDTOOut>> getReviewedContracts(@PathVariable Integer companyId, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(reviewService.getReviewedContracts(companyId));
    }


    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<ReviewDTOOut>> searchReviewsByKeyword(@PathVariable String keyword, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(reviewService.searchReviewsByKeyword(keyword));
    }

    @GetMapping("/exchange-type/{exchangeType}")
    public ResponseEntity<List<ReviewDTOOut>> getReviewsByExchangeType(@PathVariable String exchangeType, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(reviewService.getReviewsByExchangeType(exchangeType));
    }


    @GetMapping("/company/{companyId}/best-to-worst")
    public ResponseEntity<ArrayList<ReviewDTOOut>> getBestToWorstReviews(@PathVariable Integer companyId, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(reviewService.getBestToWorstReviews(companyId));
    }
}