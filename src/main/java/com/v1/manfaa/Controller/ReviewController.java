package com.v1.manfaa.Controller;
import com.v1.manfaa.Api.ApiResponse;
import com.v1.manfaa.DTO.In.ReviewDTOIn;
import com.v1.manfaa.DTO.Out.ReviewDTOOut;
import com.v1.manfaa.Service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/get")
    public ResponseEntity<List<ReviewDTOOut>> getAllReviews() {
        return ResponseEntity.status(200).body(reviewService.getAllReviews());
    }

    @PostMapping("/add/{reviewerCompanyId}/{reviewedCompanyId}/{contractAgreementId}")
    public ResponseEntity<?> addReview(@PathVariable Integer reviewerCompanyId, @PathVariable Integer reviewedCompanyId, @PathVariable Integer contractAgreementId, @RequestBody @Valid ReviewDTOIn reviewDTOIn) {
        reviewService.addReview(reviewerCompanyId, reviewedCompanyId, contractAgreementId, reviewDTOIn);
        return ResponseEntity.ok(new ApiResponse("Review added successfully"));
    }

    @PutMapping("/update/{reviewId}/{reviewerCompanyId}/{reviewedCompanyId}/{contractAgreementId}")
    public ResponseEntity<?> updateReview(@PathVariable Integer reviewId, @PathVariable Integer reviewerCompanyId, @PathVariable Integer reviewedCompanyId, @PathVariable Integer contractAgreementId, @RequestBody @Valid ReviewDTOIn reviewDTOIn) {

        reviewService.updateReview(reviewId, reviewerCompanyId, reviewedCompanyId, contractAgreementId, reviewDTOIn);
        return ResponseEntity.ok(new ApiResponse("Review updated successfully"));
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Integer reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(new ApiResponse("Review deleted successfully"));
    }
}
