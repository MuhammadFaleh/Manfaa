package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.ReviewDTOIn;
import com.v1.manfaa.DTO.Out.ReviewDTOOut;
import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.ContractAgreement;
import com.v1.manfaa.Model.Review;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ContractAgreementRepository contractAgreementRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final UserRepository userRepository;


    public ReviewDTOOut convertToDto(Review review) {
        return new ReviewDTOOut(
                review.getId(),
                review.getRating(),
                review.getDescription(),
                review.getCreated_at(),
                review.getReviewerProfile().getId(),
                review.getReviewerProfile().getName(),
                review.getReviewerProfile().getIndustry(),
                review.getReviewedProfile().getId(),
                review.getReviewedProfile().getName(),
                review.getReviewedProfile().getIndustry(),
                review.getContractAgreement().getId(),
                review.getContractAgreement().getExchangeType()
        );
    }

    public List<ReviewDTOOut> convertToDtoOut(List<Review> reviews) {
        return reviews.stream()
                .map(this::convertToDto)
                .toList();
    }


    public List<ReviewDTOOut> getAllReviews(Integer adminId) {
        User user = userRepository.findUserById(adminId);
        return convertToDtoOut(reviewRepository.findAll());
    }

    public ReviewDTOOut getReviewById(Integer reviewId , Integer companyId) {
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(companyId);
        Review review = reviewRepository.findReviewById(reviewId);

        if (review == null) {
            throw new ApiException("Review not found");
        }
        if(companyProfile == null){
            throw new ApiException("Company not found");
        }

        return convertToDto(review);
    }

    public void addReview(Integer reviewerCompanyId, Integer reviewedCompanyId, Integer contractAgreementId, ReviewDTOIn reviewDTOIn) {
        CompanyProfile reviewer = companyProfileRepository.findCompanyProfileById(reviewerCompanyId);
        CompanyProfile reviewed = companyProfileRepository.findCompanyProfileById(reviewedCompanyId);
        ContractAgreement contractAgreement = contractAgreementRepository.findContractAgreementById(contractAgreementId);

        if (reviewer == null) {
            throw new ApiException("Reviewer company not found");
        }
        if (reviewed == null) {
            throw new ApiException("Reviewed company not found");
        }
        if (contractAgreement == null) {
            throw new ApiException("Contract agreement not found");
        }
        if (reviewer.getId().equals(reviewed.getId())) {
            throw new ApiException("Company cannot review itself");
        }
        if (!contractAgreement.getStatus().equalsIgnoreCase("COMPLETED")
                && !contractAgreement.getStatus().equalsIgnoreCase("DISPUTED")) {
            throw new ApiException("Contract status must be COMPLETED or DISPUTED to submit a review");
        }

        Review existingReview = reviewRepository.findReviewByContractAgreementIdAndReviewerProfileId(contractAgreementId,reviewerCompanyId); // needs updating
        if (existingReview != null) {
            throw new ApiException("This contract already has a review");
        }

        Review review = new Review(
                null,
                reviewDTOIn.getRating(),
                reviewDTOIn.getDescription(),
                LocalDateTime.now(),
                reviewer,
                reviewed,
                contractAgreement
        );

        reviewRepository.save(review);
    }

    public void updateReview(Integer userId, Integer reviewId, ReviewDTOIn reviewDTOIn) {
        CompanyProfile company = companyProfileRepository.findCompanyProfileById(userId);
        Review review = reviewRepository.findReviewById(reviewId);

        if (company == null) {
            throw new ApiException("Company not found");
        }
        if (review == null) {
            throw new ApiException("Review not found");
        }
        if (!review.getReviewerProfile().getId().equals(company.getId())) {
            throw new ApiException("You can only update your own reviews");
        }

        review.setRating(reviewDTOIn.getRating());
        review.setDescription(reviewDTOIn.getDescription());

        reviewRepository.save(review);
    }

    public void deleteReview(Integer userId, Integer reviewId) {
        CompanyProfile company = companyProfileRepository.findCompanyProfileById(userId);
        User user = userRepository.findUserById(userId);
        Review review = reviewRepository.findReviewById(reviewId);

        if (company == null && user == null) {
            throw new ApiException("Company not found");
        }
        if (review == null) {
            throw new ApiException("Review not found");
        }
        if (!review.getReviewerProfile().getId().equals(company.getId()) && !user.getRole().equalsIgnoreCase("ADMIN")) {
            throw new ApiException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }


    public List<ReviewDTOOut> getReceivedReviewsByCompany(Integer companyId) {
        CompanyProfile company = companyProfileRepository.findCompanyProfileById(companyId);

        if (company == null) {
            throw new ApiException("Company not found");
        }

        List<Review> reviews = reviewRepository.findAllByReviewedProfile(company);
        return convertToDtoOut(reviews);
    }

    public List<ReviewDTOOut> getWrittenReviewsByCompany(Integer companyId) {
        CompanyProfile company = companyProfileRepository.findCompanyProfileById(companyId);

        if (company == null) {
            throw new ApiException("Company not found");
        }

        List<Review> reviews = reviewRepository.findAllByReviewerProfile(company);
        return convertToDtoOut(reviews);
    }


    public List<ReviewDTOOut> getReviewedContracts(Integer companyId) {
        CompanyProfile company = companyProfileRepository.findCompanyProfileById(companyId);

        if (company == null) {
            throw new ApiException("Company not found");
        }

        List<Review> reviews = reviewRepository.findAllByReviewerProfile(company);
        return convertToDtoOut(reviews);
    }


    public List<ReviewDTOOut> searchReviewsByKeyword(String keyword) {
        List<Review> reviews = reviewRepository.findAllByDescriptionContainingIgnoreCase(keyword);
        return convertToDtoOut(reviews);
    }

    public List<ReviewDTOOut> getReviewsByExchangeType(String exchangeType) {
        List<Review> reviews = reviewRepository.findByExchangeType(exchangeType);
        return convertToDtoOut(reviews);
    }


    public ArrayList<ReviewDTOOut> getBestToWorstReviews(Integer companyId) {
        CompanyProfile company = companyProfileRepository.findCompanyProfileById(companyId);

        if (company == null) {
            throw new ApiException("Company not found");
        }

        List<Review> reviews = reviewRepository.findReviewsByCompanyOrderedBestToWorst(companyId);

        ArrayList<ReviewDTOOut> reviewDTOs = new ArrayList<>();
        for (Review review : reviews) {
            reviewDTOs.add(convertToDto(review));
        }

        return reviewDTOs;
    }
}