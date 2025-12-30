package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.ReviewDTOIn;
import com.v1.manfaa.DTO.Out.ReviewDTOOut;
import com.v1.manfaa.Model.*;
import com.v1.manfaa.Repository.*;
import jakarta.transaction.Transactional;
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
    private final EmailService emailService;
    private final CompanyCreditRepository companyCreditRepository;
    private final CreditTransactionRepository creditTransactionRepository;


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

    public ReviewDTOOut getReviewById(Integer reviewId, Integer companyId) {
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(companyId);
        Review review = reviewRepository.findReviewById(reviewId);

        if (review == null) {
            throw new ApiException("Review not found");
        }
        if (companyProfile == null) {
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
        if (!contractAgreement.getStatus().equalsIgnoreCase("COMPLETED")) {
            throw new ApiException("Contract status must be COMPLETED to submit a review");
        }

        Review existingReview = reviewRepository.findReviewByContractAgreement(contractAgreement);
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
        Review review = reviewRepository.findReviewById(reviewId);

        if (company == null) {
            throw new ApiException("Company not found");
        }
        if (review == null) {
            throw new ApiException("Review not found");
        }
        if (!review.getReviewerProfile().getId().equals(company.getId())) {
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


    @Transactional
    public void createPendingReviewsOnCompletion(Integer contractAgreementId) {
        ContractAgreement contractAgreement = contractAgreementRepository.findContractAgreementById(contractAgreementId);

        if (contractAgreement == null) {
            throw new ApiException("Contract agreement not found");
        }

        // Verify contract is COMPLETED or DISPUTED
        if (!contractAgreement.getStatus().equalsIgnoreCase("COMPLETED") &&
                !contractAgreement.getStatus().equalsIgnoreCase("DISPUTED")) {
            throw new ApiException("Contract must be COMPLETED or DISPUTED to create reviews");
        }

        CompanyProfile requester = contractAgreement.getRequesterCompanyProfile();
        CompanyProfile provider = contractAgreement.getProviderCompanyProfile();

        // Check if reviews already exist for this contract
        List<Review> existingReviews = reviewRepository.findAllByContractAgreement(contractAgreement);
        if (!existingReviews.isEmpty()) {
            System.out.println("⚠️ Reviews already exist for contract #" + contractAgreementId);
            return;
        }

        // Create Review 1: Requester reviews Provider
        Review requesterReview = new Review(
                null,
                0, // Rating 0 means pending/not submitted yet
                "PENDING - Review not yet submitted", // Placeholder description
                LocalDateTime.now(),
                requester, // Reviewer
                provider,  // Being reviewed
                contractAgreement
        );

        // Create Review 2: Provider reviews Requester
        Review providerReview = new Review(
                null,
                0, // Rating 0 means pending/not submitted yet
                "PENDING - Review not yet submitted", // Placeholder description
                LocalDateTime.now(),
                provider, // Reviewer
                requester, // Being reviewed
                contractAgreement
        );

        reviewRepository.save(requesterReview);
        reviewRepository.save(providerReview);

        // Send notification to REQUESTER
        String requesterSubject = "Contract " + contractAgreement.getStatus() + " - Please Submit Your Review";
        String requesterMessage = String.format(
                "Dear %s,\n\n" +
                        "The contract #%d has been marked as %s.\n\n" +
                        "Contract Details:\n" +
                        "- Contract ID: %d\n" +
                        "- Service Request: %s\n" +
                        "- Provider Company: %s\n" +
                        "- Exchange Type: %s\n" +
                        "- Start Date: %s\n" +
                        "- End Date: %s\n" +
                        "- Status: %s\n\n" +
                        "You can now submit a review for the provider company (%s).\n\n" +
                        "Please log in to the platform to rate and provide feedback on your experience with this service.\n" +
                        "Your review will help other companies make informed decisions.\n\n" +
                        "Best regards,\n" +
                        "Manfaa Support Team",
                requester.getName(),
                contractAgreement.getId(),
                contractAgreement.getStatus(),
                contractAgreement.getId(),
                contractAgreement.getServiceRequest().getTitle(),
                provider.getName(),
                contractAgreement.getExchangeType(),
                contractAgreement.getStartDate(),
                contractAgreement.getEndDate(),
                contractAgreement.getStatus(),
                provider.getName()
        );

        emailService.sendEmail(requester.getUser().getEmail(), requesterSubject, requesterMessage);

        // Send notification to PROVIDER
        String providerSubject = "Contract " + contractAgreement.getStatus() + " - Please Submit Your Review";
        String providerMessage = String.format(
                "Dear %s,\n\n" +
                        "The contract #%d has been marked as %s.\n\n" +
                        "Contract Details:\n" +
                        "- Contract ID: %d\n" +
                        "- Service Request: %s\n" +
                        "- Requester Company: %s\n" +
                        "- Exchange Type: %s\n" +
                        "- Start Date: %s\n" +
                        "- End Date: %s\n" +
                        "- Status: %s\n\n" +
                        "You can now submit a review for the requester company (%s).\n\n" +
                        "Please log in to the platform to rate and provide feedback on your experience with this contract.\n" +
                        "Your review will help other companies make informed decisions.\n\n" +
                        "Best regards,\n" +
                        "Manfaa Support Team",
                provider.getName(),
                contractAgreement.getId(),
                contractAgreement.getStatus(),
                contractAgreement.getId(),
                contractAgreement.getServiceRequest().getTitle(),
                requester.getName(),
                contractAgreement.getExchangeType(),
                contractAgreement.getStartDate(),
                contractAgreement.getEndDate(),
                contractAgreement.getStatus(),
                requester.getName()
        );

        emailService.sendEmail(provider.getUser().getEmail(), providerSubject, providerMessage);

    }


    @Transactional
    public void handleRejectionAndRefund(Integer contractAgreementId) {
        ContractAgreement contractAgreement = contractAgreementRepository.findContractAgreementById(contractAgreementId);

        if (contractAgreement == null) {
            throw new ApiException("Contract agreement not found");
        }

        // Verify that at least one party has rejected
        if (!contractAgreement.getFirstPartyAgreement().equalsIgnoreCase("REJECTED") &&
                !contractAgreement.getSecondPartyAgreement().equalsIgnoreCase("REJECTED")) {
            throw new ApiException("Contract has not been rejected by any party");
        }

        CompanyProfile requester = contractAgreement.getRequesterCompanyProfile();
        CompanyProfile provider = contractAgreement.getProviderCompanyProfile();

        // Determine who rejected
        String rejectedBy = "";
        if ("REJECTED".equalsIgnoreCase(contractAgreement.getFirstPartyAgreement())) {
            rejectedBy = requester.getName() + " (Requester)";
        } else if ("REJECTED".equalsIgnoreCase(contractAgreement.getSecondPartyAgreement())) {
            rejectedBy = provider.getName() + " (Provider)";
        }

        // Update contract status to CANCELLED
        contractAgreement.setStatus("CANCELLED");
        contractAgreement.setClosedAt(LocalDateTime.now());
        contractAgreementRepository.save(contractAgreement);

        // REFUND TOKENS if exchange type is TOKENS
        boolean tokensRefunded = false;
        Double refundAmount = 0.0;

        if ("TOKENS".equalsIgnoreCase(contractAgreement.getExchangeType())) {
            CreditTransaction creditTransaction = contractAgreement.getCreditTransaction();

            if (creditTransaction != null && creditTransaction.getStatus().equalsIgnoreCase("PENDING")) {
                // Refund tokens to the paying company (requester)
                CompanyCredit payingCompanyCredit = creditTransaction.getPayingCompany();
                refundAmount = creditTransaction.getAmount();

                // Add the tokens back to the paying company's balance
                payingCompanyCredit.setBalance(payingCompanyCredit.getBalance() + refundAmount);
                companyCreditRepository.save(payingCompanyCredit);

                // Update transaction status to CANCELLED
                creditTransaction.setStatus("CANCELLED");
                creditTransactionRepository.save(creditTransaction);

                tokensRefunded = true;
                System.out.println(" TOKENS REFUNDED: " + refundAmount + " tokens returned to " +
                        payingCompanyCredit.getCompanyProfile().getName());
            }
        }

        // Send notification to REQUESTER
        String requesterSubject = "Contract #" + contractAgreement.getId() + " Has Been Rejected";
        String requesterMessage = String.format(
                "Dear %s,\n\n" +
                        "We regret to inform you that contract agreement #%d has been rejected.\n\n" +
                        "Contract Details:\n" +
                        "- Contract ID: %d\n" +
                        "- Service Request: %s\n" +
                        "- Provider Company: %s\n" +
                        "- Rejected By: %s\n" +
                        "- Exchange Type: %s\n" +
                        "- Status: CANCELLED\n\n" +
                        "%s" +
                        "The service request will be reopened and you can review other bids or create new contracts.\n\n" +
                        "If you have any questions, please contact our support team.\n\n" +
                        "Best regards,\n" +
                        "Manfaa Support Team",
                requester.getName(),
                contractAgreement.getId(),
                contractAgreement.getId(),
                contractAgreement.getServiceRequest().getTitle(),
                provider.getName(),
                rejectedBy,
                contractAgreement.getExchangeType(),
                tokensRefunded
                        ? " Your tokens have been REFUNDED: " + refundAmount + " tokens have been returned to your account.\n\n"
                        : ""
        );

        emailService.sendEmail(requester.getUser().getEmail(), requesterSubject, requesterMessage);

        // Send notification to PROVIDER
        String providerSubject = "Contract #" + contractAgreement.getId() + " Has Been Rejected";
        String providerMessage = String.format(
                "Dear %s,\n\n" +
                        "We regret to inform you that contract agreement #%d has been rejected.\n\n" +
                        "Contract Details:\n" +
                        "- Contract ID: %d\n" +
                        "- Service Request: %s\n" +
                        "- Requester Company: %s\n" +
                        "- Rejected By: %s\n" +
                        "- Status: CANCELLED\n\n" +
                        "This contract has been closed. You can continue to bid on other service requests.\n\n" +
                        "If you have any questions, please contact our support team.\n\n" +
                        "Best regards,\n" +
                        "Manfaa Support Team",
                provider.getName(),
                contractAgreement.getId(),
                contractAgreement.getId(),
                contractAgreement.getServiceRequest().getTitle(),
                requester.getName(),
                rejectedBy
        );

        emailService.sendEmail(provider.getUser().getEmail(), providerSubject, providerMessage);

        System.out.println("Notifications sent to both parties");
    }
}

