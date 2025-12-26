package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.ReviewDTOIn;
import com.v1.manfaa.DTO.Out.ReviewDTOOut;
import com.v1.manfaa.DTO.Out.UserDTOOut;
import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.ContractAgreement;
import com.v1.manfaa.Model.Review;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Repository.*;
import lombok.AllArgsConstructor;
import org.springframework.lang.Contract;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ContractAgreementRepository contractAgreementRepository;
    private final CompanyProfileRepository companyProfileRepository;


    public List<ReviewDTOOut> convertToDtoOut(List<Review> reviews) {
        return reviews.stream()
                .map(review -> new ReviewDTOOut(
                       review.getId(),
                        review.getDescription()
                ))
                .toList();
    }
    public List<ReviewDTOOut>getAllReviews(){
        return convertToDtoOut(reviewRepository.findAll());
    }


    public void addReview(Integer reviewer_company_id,Integer reviewed_company_id , Integer contractAgreementId , ReviewDTOIn reviewDTOIn){
        ContractAgreement contractAgreement = contractAgreementRepository.findContractAgreementById(contractAgreementId);
        CompanyProfile reviewer = companyProfileRepository.findCompanyProfileById(reviewer_company_id);
        CompanyProfile reviewed = companyProfileRepository.findCompanyProfileById(reviewed_company_id);


        if(contractAgreement == null){
            throw new ApiException("contractAgreement not found ");
        }
        if(reviewer == null){
            throw new ApiException("reviewer not found ");
        }


        if(reviewed == null){
            throw new ApiException("reviewed not found ");
        }
        if(reviewer.getId().equals(reviewed.getId())){
            throw new ApiException("reviewer cant rate him self");
        }

        if(!contractAgreement.getStatus().equalsIgnoreCase("COMPLETED")){
            throw new ApiException("Contract status must be COMPLETED");
        }
        Review exist = reviewRepository.findReviewByContractAgreement(contractAgreement);
        if(exist!=null){
            throw new ApiException("this Contract already has review ");

        }
        Review review = new Review(null,reviewDTOIn.getRating(),reviewDTOIn.getDescription(), LocalDateTime.now(),reviewer,reviewed,contractAgreement);

        reviewRepository.save(review);
    }



    public void updateReview(Integer oldReview ,Integer reviewer_company_id,Integer reviewed_company_id , Integer contractAgreementId , ReviewDTOIn reviewDTOIn) {
        Review review = reviewRepository.findReviewById(oldReview);
        ContractAgreement contractAgreement = contractAgreementRepository.findContractAgreementById(contractAgreementId);
        CompanyProfile reviewer = companyProfileRepository.findCompanyProfileById(reviewer_company_id);
        CompanyProfile reviewed = companyProfileRepository.findCompanyProfileById(reviewed_company_id);

        if(review == null){
            throw new ApiException("review not found");
        }
        if(!review.getId().equals(oldReview)){
            throw new ApiException("review ID Doesn't Match");
        }
        if(contractAgreement == null){
            throw new ApiException("contractAgreement not found ");
        }
        if(reviewer == null){
            throw new ApiException("reviewer not found ");
        }

        if(reviewed == null){
            throw new ApiException("reviewed not found ");
        }

        if(!contractAgreement.getStatus().equalsIgnoreCase("COMPLETED")){
            throw new ApiException("Contract status must be COMPLETED");
        }
        review.setRating(reviewDTOIn.getRating());
        review.setDescription(reviewDTOIn.getDescription());

        reviewRepository.save(review);


    }

    public void deleteReview(Integer reviewId){
        Review review  =reviewRepository.findReviewById(reviewId);

        if(review == null){
            throw new ApiException("review not found");
        }
        reviewRepository.delete(review);
    }
}
