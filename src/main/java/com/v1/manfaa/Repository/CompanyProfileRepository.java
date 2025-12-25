package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Integer> {

    CompanyProfile findCompanyProfileById(Integer id);
}
