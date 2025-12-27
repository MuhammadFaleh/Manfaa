package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.ContractAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractAgreementRepository extends JpaRepository<ContractAgreement, Integer> {
    ContractAgreement findContractAgreementById(Integer id);
}
