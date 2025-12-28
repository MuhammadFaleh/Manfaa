package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.ContractAgreement;
import com.v1.manfaa.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {


    Review findReviewById(Integer id);

    Review findReviewByContractAgreement(ContractAgreement contractAgreement);

    List<Review> findAllByReviewedProfile(CompanyProfile company);

    List<Review> findAllByReviewerProfile(CompanyProfile company);

    List<Review> findAllByDescriptionContainingIgnoreCase(String keyword);

    @Query("SELECT r FROM Review r WHERE r.contractAgreement.exchangeType = :exchangeType")
    List<Review> findByExchangeType(@Param("exchangeType") String exchangeType);

    @Query("SELECT r FROM Review r WHERE r.reviewedProfile.id = :companyId ORDER BY r.rating DESC, r.created_at DESC")
    List<Review> findReviewsByCompanyOrderedBestToWorst(@Param("companyId") Integer companyId);
}