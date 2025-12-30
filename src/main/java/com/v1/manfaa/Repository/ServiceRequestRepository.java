package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Integer> {
    List<ServiceRequest> findServiceRequestsByCompanyProfile(CompanyProfile companyProfile);
    List<ServiceRequest> findServiceRequestsByCompanyProfileId(Integer id);
    List<ServiceRequest> findServiceRequestsByCompanyProfileAndStatus(CompanyProfile companyProfile, String status);
    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.status = 'OPEN' AND (LOWER(sr.title) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(sr.description) LIKE LOWER(CONCAT('%', ?1, '%')))")
    List<ServiceRequest> searchServiceRequestsByKeyword(String keyword);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.status = 'OPEN' AND sr.category.id = ?1")
    List<ServiceRequest> findServiceRequestsByCategoryId(Integer categoryId);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.status = 'OPEN' AND sr.exchangeType = ?1")
    List<ServiceRequest> findServiceRequestsByExchangeType(String exchangeType);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.status = 'OPEN' AND sr.proposedStartDate BETWEEN ?1 AND ?2")
    List<ServiceRequest> findServiceRequestsByProposedDateRange(LocalDate startDate, LocalDate endDate);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.status = 'OPEN' AND sr.tokenAmount BETWEEN ?1 AND ?2")
    List<ServiceRequest> findServiceRequestsByTokenAmountRange(Double minAmount, Double maxAmount);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.status = 'OPEN' ORDER BY sr.tokenAmount ASC")
    List<ServiceRequest> findAllByOrderByTokenAmountAsc();

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.status = 'OPEN' ORDER BY sr.tokenAmount DESC")
    List<ServiceRequest> findAllByOrderByTokenAmountDesc();

    ServiceRequest findServiceRequestById(Integer id);

    List<ServiceRequest> findServiceRequestByCompanyProfileIsSubscriber(Boolean isSubscriber);




}
