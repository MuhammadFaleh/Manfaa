package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.Skills;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillsRepository extends JpaRepository<Skills, Integer> {

    Skills findSkillsById(Integer id);
    Skills findSkillsByName(String name);

    List<Skills> findAllByCompanyProfile(CompanyProfile companyProfile);

    List<Skills> findAllByNameContainingIgnoreCase(String keyword);

    List<Skills> findAllByDescriptionContainingIgnoreCase(String keyword);


}