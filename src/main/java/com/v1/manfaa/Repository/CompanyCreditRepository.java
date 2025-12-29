package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.CompanyCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyCreditRepository extends JpaRepository<CompanyCredit, Integer> {

    CompanyCredit findCompanyCreditById(Integer id);
    CompanyCredit findCompanyCreditByUserId(Integer id);

}
