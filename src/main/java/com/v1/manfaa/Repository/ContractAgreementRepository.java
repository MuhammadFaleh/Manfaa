package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.ContractAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractAgreementRepository extends JpaRepository<ContractAgreement, Integer> {
    ContractAgreement findContractAgreementById(Integer id);
    List<ContractAgreement> findContractAgreementByServiceRequestId(Integer id);
    List<ContractAgreement> findContractAgreementByServiceBidId(Integer id);
}
