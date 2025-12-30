package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.ContractAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContractAgreementRepository extends JpaRepository<ContractAgreement, Integer> {
    ContractAgreement findContractAgreementById(Integer id);
    List<ContractAgreement> findContractAgreementByServiceRequestId(Integer id);
    List<ContractAgreement> findContractAgreementByServiceBidId(Integer id);
    @Query("SELECT c FROM ContractAgreement c WHERE c.providerCompanyProfile.id = :id OR c.requesterCompanyProfile.id = :id")
    List<ContractAgreement> findContractAgreementByCompanyProfileId(Integer id);
}
