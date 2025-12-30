package com.v1.manfaa.testing;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.ReviewDTOIn;
import com.v1.manfaa.DTO.Out.ReviewDTOOut;
import com.v1.manfaa.Model.*;
import com.v1.manfaa.Repository.*;
import com.v1.manfaa.Service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ContractAgreementRepository contractAgreementRepository;

    @Mock
    private CompanyProfileRepository companyProfileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private CompanyProfile reviewerCompany;
    private CompanyProfile reviewedCompany;
    private ContractAgreement contractAgreement;
    private Review review;
    private ReviewDTOIn reviewDTOIn;

    @BeforeEach
    void setUp() {
        reviewerCompany = new CompanyProfile();
        reviewerCompany.setId(1);
        reviewerCompany.setName("Reviewer Company");
        reviewerCompany.setIndustry("IT");

        reviewedCompany = new CompanyProfile();
        reviewedCompany.setId(2);
        reviewedCompany.setName("Reviewed Company");
        reviewedCompany.setIndustry("Marketing");

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setId(1);
        serviceRequest.setTitle("Test Request");

        ServiceBid serviceBid = new ServiceBid();
        serviceBid.setId(1);

        contractAgreement = new ContractAgreement();
        contractAgreement.setId(1);
        contractAgreement.setStatus("COMPLETED");
        contractAgreement.setExchangeType("TOKENS");
        contractAgreement.setRequesterCompanyProfile(reviewerCompany);
        contractAgreement.setProviderCompanyProfile(reviewedCompany);
        contractAgreement.setServiceRequest(serviceRequest);
        contractAgreement.setServiceBid(serviceBid);

        reviewDTOIn = new ReviewDTOIn(4.0, "Great service!", LocalDate.now());


        review = new Review();
        review.setId(1);
        review.setRating(4.0);
        review.setDescription("Great service!");
        review.setCreated_at(LocalDateTime.now());
        review.setReviewerProfile(reviewerCompany);
        review.setReviewedProfile(reviewedCompany);
        review.setContractAgreement(contractAgreement);
    }

    // ADD REVIEW TESTS

    @Test
    void addReview_Success() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(reviewerCompany);
        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(reviewedCompany);
        when(contractAgreementRepository.findContractAgreementById(1)).thenReturn(contractAgreement);
        when(reviewRepository.findReviewByContractAgreementIdAndReviewerProfileId(1, 1)).thenReturn(null);

        reviewService.addReview(1, 2, 1, reviewDTOIn);

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void addReview_ThrowsException_ReviewerCompanyNotFound() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            reviewService.addReview(1, 2, 1, reviewDTOIn);
        });

        assertEquals("Reviewer company not found", exception.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void addReview_ThrowsException_ReviewedCompanyNotFound() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(reviewerCompany);
        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            reviewService.addReview(1, 2, 1, reviewDTOIn);
        });

        assertEquals("Reviewed company not found", exception.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void addReview_ThrowsException_ContractNotFound() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(reviewerCompany);
        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(reviewedCompany);
        when(contractAgreementRepository.findContractAgreementById(1)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            reviewService.addReview(1, 2, 1, reviewDTOIn);
        });

        assertEquals("Contract agreement not found", exception.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void addReview_ThrowsException_CompanyReviewingItself() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(reviewerCompany);
        when(contractAgreementRepository.findContractAgreementById(1)).thenReturn(contractAgreement);

        ApiException exception = assertThrows(ApiException.class, () -> {
            reviewService.addReview(1, 1, 1, reviewDTOIn);
        });

        assertEquals("Company cannot review itself", exception.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void addReview_ThrowsException_ContractNotCompleted() {
        contractAgreement.setStatus("ACTIVE");

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(reviewerCompany);
        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(reviewedCompany);
        when(contractAgreementRepository.findContractAgreementById(1)).thenReturn(contractAgreement);

        ApiException exception = assertThrows(ApiException.class, () -> {
            reviewService.addReview(1, 2, 1, reviewDTOIn);
        });

        assertEquals("Contract status must be COMPLETED or DISPUTED to submit a review", exception.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void addReview_Success_ContractDisputed() {
        contractAgreement.setStatus("DISPUTED");

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(reviewerCompany);
        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(reviewedCompany);
        when(contractAgreementRepository.findContractAgreementById(1)).thenReturn(contractAgreement);
        when(reviewRepository.findReviewByContractAgreementIdAndReviewerProfileId(1, 1)).thenReturn(null);

        reviewService.addReview(1, 2, 1, reviewDTOIn);

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void addReview_ThrowsException_ReviewAlreadyExists() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(reviewerCompany);
        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(reviewedCompany);
        when(contractAgreementRepository.findContractAgreementById(1)).thenReturn(contractAgreement);
        when(reviewRepository.findReviewByContractAgreementIdAndReviewerProfileId(1, 1)).thenReturn(review);

        ApiException exception = assertThrows(ApiException.class, () -> {
            reviewService.addReview(1, 2, 1, reviewDTOIn);
        });

        assertEquals("This contract already has a review", exception.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    // UPDATE REVIEW TESTS

    @Test
    void updateReview_Success() {
        ReviewDTOIn updateDTO = new ReviewDTOIn(5.0, "Updated review - Excellent!", LocalDate.now());
        updateDTO.setRating(5.0);
        updateDTO.setDescription("Updated review - Excellent!");

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(reviewerCompany);
        when(reviewRepository.findReviewById(1)).thenReturn(review);

        reviewService.updateReview(1, 1, updateDTO);

        verify(reviewRepository, times(1)).save(review);
        assertEquals(5, review.getRating());
        assertEquals("Updated review - Excellent!", review.getDescription());
    }

    @Test
    void updateReview_ThrowsException_CompanyNotFound() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            reviewService.updateReview(1, 1, reviewDTOIn);
        });

        assertEquals("Company not found", exception.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void updateReview_ThrowsException_ReviewNotFound() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(reviewerCompany);
        when(reviewRepository.findReviewById(1)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            reviewService.updateReview(1, 1, reviewDTOIn);
        });

        assertEquals("Review not found", exception.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void updateReview_ThrowsException_UnauthorizedUser() {
        CompanyProfile differentCompany = new CompanyProfile();
        differentCompany.setId(999);

        when(companyProfileRepository.findCompanyProfileById(999)).thenReturn(differentCompany);
        when(reviewRepository.findReviewById(1)).thenReturn(review);
        ApiException exception = assertThrows(ApiException.class, () -> {
            reviewService.updateReview(999, 1, reviewDTOIn);
        });

        assertEquals("You can only update your own reviews", exception.getMessage());
        verify(reviewRepository, never()).save(any());
    }

// DELETE REVIEW TESTS

    @Test
    void deleteReview_Success_ByOwner() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(reviewerCompany);
        when(userRepository.findUserById(1)).thenReturn(null);
        when(reviewRepository.findReviewById(1)).thenReturn(review);

        reviewService.deleteReview(1, 1);

        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    void deleteReview_Success_ByAdmin() {
        User admin = new User();
        admin.setId(3);
        admin.setRole("ADMIN");

        CompanyProfile someCompany = new CompanyProfile();
        someCompany.setId(3);

        when(companyProfileRepository.findCompanyProfileById(3)).thenReturn(someCompany);
        when(userRepository.findUserById(3)).thenReturn(admin);
        when(reviewRepository.findReviewById(1)).thenReturn(review);

        reviewService.deleteReview(3, 1);

        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    void deleteReview_ThrowsException_CompanyAndUserNotFound() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(null);
        when(userRepository.findUserById(1)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            reviewService.deleteReview(1, 1);
        });

        assertEquals("Company not found", exception.getMessage());
        verify(reviewRepository, never()).delete(any());
    }

    @Test
    void deleteReview_ThrowsException_ReviewNotFound() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(reviewerCompany);
        when(userRepository.findUserById(1)).thenReturn(null);
        when(reviewRepository.findReviewById(1)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            reviewService.deleteReview(1, 1);
        });

        assertEquals("Review not found", exception.getMessage());
        verify(reviewRepository, never()).delete(any());
    }

    @Test
    void deleteReview_ThrowsException_UnauthorizedUser() {
        CompanyProfile differentCompany = new CompanyProfile();
        differentCompany.setId(999);

        User regularUser = new User();
        regularUser.setId(999);
        regularUser.setRole("COMPANY");

        when(companyProfileRepository.findCompanyProfileById(999)).thenReturn(differentCompany);
        when(userRepository.findUserById(999)).thenReturn(regularUser);
        when(reviewRepository.findReviewById(1)).thenReturn(review);

        ApiException exception = assertThrows(ApiException.class, () -> {
            reviewService.deleteReview(999, 1);
        });

        assertEquals("You can only delete your own reviews", exception.getMessage());
        verify(reviewRepository, never()).delete(any());
    }

// GET REVIEWS TESTS

    @Test
    void getReceivedReviewsByCompany_Success() {
        List<Review> reviews = Arrays.asList(review);

        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(reviewedCompany);
        when(reviewRepository.findAllByReviewedProfile(reviewedCompany)).thenReturn(reviews);

        List<ReviewDTOOut> result = reviewService.getReceivedReviewsByCompany(2);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findAllByReviewedProfile(reviewedCompany);
    }

    @Test
    void getReceivedReviewsByCompany_ThrowsException_CompanyNotFound() {
        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            reviewService.getReceivedReviewsByCompany(2);
        });

        assertEquals("Company not found", exception.getMessage());
    }

    @Test
    void getWrittenReviewsByCompany_Success() {
        List<Review> reviews = Arrays.asList(review);

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(reviewerCompany);
        when(reviewRepository.findAllByReviewerProfile(reviewerCompany)).thenReturn(reviews);

        List<ReviewDTOOut> result = reviewService.getWrittenReviewsByCompany(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findAllByReviewerProfile(reviewerCompany);
    }

    @Test
    void getBestToWorstReviews_Success() {
        List<Review> reviews = Arrays.asList(review);

        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(reviewedCompany);
        when(reviewRepository.findReviewsByCompanyOrderedBestToWorst(2)).thenReturn(reviews);

        List<ReviewDTOOut> result = reviewService.getBestToWorstReviews(2);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findReviewsByCompanyOrderedBestToWorst(2);
    }

    @Test
    void getBestToWorstReviews_ThrowsException_CompanyNotFound() {
        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            reviewService.getBestToWorstReviews(2);
        });

        assertEquals("Company not found", exception.getMessage());
    }

    @Test
    void searchReviewsByKeyword_Success() {
        List<Review> reviews = Arrays.asList(review);

        when(reviewRepository.findAllByDescriptionContainingIgnoreCase("Great")).thenReturn(reviews);

        List<ReviewDTOOut> result = reviewService.searchReviewsByKeyword("Great");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getReviewsByExchangeType_Success() {
        List<Review> reviews = Arrays.asList(review);

        when(reviewRepository.findByExchangeType("TOKENS")).thenReturn(reviews);

        List<ReviewDTOOut> result = reviewService.getReviewsByExchangeType("TOKENS");

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}