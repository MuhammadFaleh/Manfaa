package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Integer> {
    ServiceRequest findServiceRequestById(Integer id);

    List<ServiceRequest> findServiceRequestsByCompanyProfile(CompanyProfile companyProfile);

    List<ServiceRequest> findServiceRequestsByCompanyProfileAndStatus(CompanyProfile companyProfile, String status);

    @Query("SELECT sr FROM ServiceRequest sr WHERE LOWER(sr.title) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(sr.description) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<ServiceRequest> searchServiceRequestsByKeyword(String keyword);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.category.id = ?1")
    List<ServiceRequest> findServiceRequestsByCategoryId(Integer categoryId);

    List<ServiceRequest> findServiceRequestsByExchangeType(String exchangeType);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.proposedStartDate BETWEEN ?1 AND ?2")
    List<ServiceRequest> findServiceRequestsByProposedDateRange(LocalDate startDate, LocalDate endDate);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.tokenAmount BETWEEN ?1 AND ?2")
    List<ServiceRequest> findServiceRequestsByTokenAmountRange(Double minAmount, Double maxAmount);

    List<ServiceRequest> findAllByOrderByTokenAmountAsc();

    List<ServiceRequest> findAllByOrderByTokenAmountDesc();

    List<ServiceRequest> findServiceRequestsByStatus(String status);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.status = 'OPEN' AND sr.proposedStartDate < CURRENT_DATE")
    List<ServiceRequest> findExpiredOpenServiceRequests();

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.createdAt BETWEEN ?1 AND ?2")
    List<ServiceRequest> findServiceRequestsByCreationDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime);







}
