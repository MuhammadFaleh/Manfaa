package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.SkillsDTOIn;
import com.v1.manfaa.DTO.Out.SkillsDTOOut;
import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.Skills;
import com.v1.manfaa.Repository.CompanyProfileRepository;
import com.v1.manfaa.Repository.SkillsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SkillsService {
    private final SkillsRepository skillsRepository;
    private final CompanyProfileRepository companyProfileRepository;

    public List<SkillsDTOOut> convertToDtoOut(List<Skills> skills) {
        return skills.stream()
                .map(skill -> new SkillsDTOOut(
                        skill.getName(),
                        skill.getDescription()
                ))
                .toList();
    }
    public List<SkillsDTOOut> getAllSkills(){
        return convertToDtoOut(skillsRepository.findAll());
    }


    public void addSkills(Integer companyProfileId , SkillsDTOIn skillsDTOIn){
       CompanyProfile companyProfile =  companyProfileRepository.findCompanyProfileById(companyProfileId);

       if(companyProfile == null){
           throw new ApiException("companyProfile not found");
       }
       Skills skills = new Skills(null,skillsDTOIn.getName(),skillsDTOIn.getDescription(),null);

       skills.setCompanyProfile(companyProfile);
       companyProfile.getSkills().add(skills);
       skillsRepository.save(skills);

    }

    public void updateSkills(Integer skillsId , Integer companyProfileId , SkillsDTOIn skillsDTOIn){
        CompanyProfile companyProfile =  companyProfileRepository.findCompanyProfileById(companyProfileId);

        Skills old = skillsRepository.findSkillsById(skillsId);
        if(companyProfile == null){
            throw new ApiException("companyProfile not found");
        }
        if(old == null){
            throw new ApiException("Skills not found");
        }
        if(!old.getCompanyProfile().getId().equals(companyProfileId)){
            throw new ApiException("Skills does not belong to this company profile");
        }


        old.setName(skillsDTOIn.getName());
        old.setDescription(skillsDTOIn.getDescription());

        skillsRepository.save(old);

    }

    public void deleteSkills(Integer companyProfileId , Integer skillsId){
        Skills skills = skillsRepository.findSkillsById(skillsId);
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(companyProfileId);

        if(skills == null){
            throw new ApiException("Skills not found");
        }
        if(companyProfile == null){
            throw new ApiException("companyProfile not found");
        }

        if(!skills.getCompanyProfile().getId().equals(companyProfileId)){
            throw new ApiException("Skills does not belong to this company profile");
        }

        skillsRepository.delete(skills);

    }






}
