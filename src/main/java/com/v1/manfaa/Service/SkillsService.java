package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.SkillsDTOIn;
import com.v1.manfaa.DTO.Out.SkillsDTOOut;
import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.Skills;
import com.v1.manfaa.Repository.CompanyProfileRepository;
import com.v1.manfaa.Repository.ServiceRequestRepository;
import com.v1.manfaa.Repository.SkillsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillsService {
    private final SkillsRepository skillsRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    public List<SkillsDTOOut> convertToDtoOut(List<Skills> skills) {
        return skills.stream()
                .map(skill -> new SkillsDTOOut(
                        skill.getName(),
                        skill.getDescription()
                ))
                .toList();
    }

    public List<SkillsDTOOut> getAllSkills() {
        return convertToDtoOut(skillsRepository.findAll());
    }


    public void addSkills(SkillsDTOIn skillsDTOIn) {
        Skills skills = new Skills(null, skillsDTOIn.getName(), skillsDTOIn.getDescription(), null);

        if(skillsRepository.findSkillsByName(skillsDTOIn.getName()) != null){
            throw new ApiException("skill already exist");
        }
        skillsRepository.save(skills);

    }

    public void updateSkills(Integer skillsId, SkillsDTOIn skillsDTOIn) {
        Skills old = skillsRepository.findSkillsById(skillsId);
        if (old == null) {
            throw new ApiException("Skills not found");
        }
        old.setName(skillsDTOIn.getName());
        old.setDescription(skillsDTOIn.getDescription());

        skillsRepository.save(old);

    }

    public void deleteSkills(Integer skillsId) {
        Skills skills = skillsRepository.findSkillsById(skillsId);

        if (skills == null) {
            throw new ApiException("Skills not found");
        }
        skillsRepository.delete(skills);
    }

    public void assignSkill(Integer userId, Integer skillId){
        Skills skills = skillsRepository.findSkillsById(skillId);
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(userId);

        if(skills == null || companyProfile == null){
            throw new ApiException("Skill or company not found");
        }

        if(companyProfile.getSkills().stream().anyMatch(e->e.getId().equals(skillId))){
            throw new ApiException("skill already added");
        }

        companyProfile.getSkills().add(skills);
        companyProfileRepository.save(companyProfile);
    }

    public void removeSkill(Integer userId, Integer skillId){
        Skills skills = skillsRepository.findSkillsById(skillId);
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(userId);

        if(skills == null || companyProfile == null){
            throw new ApiException("Skill or company not found");
        }
        boolean removed = companyProfile.getSkills().removeIf(e->e.getId().equals(skillId));

        if(!removed){
            throw new ApiException("skill not present");
        }

        companyProfileRepository.save(companyProfile);
    }


    public List<SkillsDTOOut> getSkillsByCompany(Integer companyId) {
        CompanyProfile company = companyProfileRepository.findCompanyProfileById(companyId);

        if (company == null) {
            throw new ApiException("Company not found");
        }

        List<Skills> skills = skillsRepository.findAllByCompanyProfile(company);
        return convertToDtoOut(skills);
    }


    public List<SkillsDTOOut> searchSkillsByKeyword(String keyword) {
        List<Skills> nameMatches = skillsRepository.findAllByNameContainingIgnoreCase(keyword);
        List<Skills> descriptionMatches = skillsRepository.findAllByDescriptionContainingIgnoreCase(keyword);

        List<Skills> allMatches = new ArrayList<>();
        allMatches.addAll(nameMatches);

        for (Skills skill : descriptionMatches) {
            if (!allMatches.contains(skill)) {
                allMatches.add(skill);
            }
        }

        return convertToDtoOut(allMatches);
    }
}



