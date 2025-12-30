package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.CompanyProfileDTOIn;
import com.v1.manfaa.DTO.In.RegisterDTOIn;
import com.v1.manfaa.DTO.Out.CompanyFullInfoDTOOut;
import com.v1.manfaa.DTO.Out.CompanyProfileDTOOut;
import com.v1.manfaa.DTO.Out.SkillsDTOOut;
import com.v1.manfaa.Model.*;
import com.v1.manfaa.Repository.CompanyCreditRepository;
import com.v1.manfaa.Repository.CompanyProfileRepository;
import com.v1.manfaa.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CompanyProfileService {

    private final CompanyProfileRepository companyProfileRepository;
    private final UserRepository userRepository;
    private final CompanyCreditRepository companyCreditRepository;

    public List<CompanyProfileDTOOut> getAllCompanyProfiles() {
        return convertToDtoOut(companyProfileRepository.findAll());
    }

    @Transactional
    public void registerCompany(RegisterDTOIn dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ApiException("Email already exists");
        }

        String hash = new BCryptPasswordEncoder().encode(dto.getPassword());
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(hash);
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setPhone_Number(dto.getPhoneNumber());
        user.setRole("COMPANY");
        user.setRecordNumber(dto.getRecordNumber());

        // new change (ibra& nawaf)
        userRepository.save(user);

        CompanyCredit companyCredit = new CompanyCredit();
        companyCredit.setBalance(0.0);
        companyCredit.setTotalEarned(0.0);
        companyCredit.setTotalSpent(0.0);

        CompanyProfile company = new CompanyProfile();
        company.setName(dto.getCompanyName());
        company.setIndustry(dto.getIndustry());
        company.setTeamSize(dto.getTeamSize());
        company.setDescription(dto.getDescription());
        company.setCreatedAt(LocalDateTime.now());
        company.setIsSubscriber(false);



        // Set bidirectional relationships
        company.setCompanyCredit(companyCredit);
        companyCredit.setCompanyProfile(company);

        company.setUser(user);
        user.setCompanyProfile(company);

        // Save user first (cascades to company and credit)
        userRepository.save(user);
    }

    public void updateCompanyProfile(Integer companyProfileId, CompanyProfileDTOIn dto) {
        CompanyProfile oldCompany = companyProfileRepository.findCompanyProfileById(companyProfileId);
        if (oldCompany == null)
            throw new ApiException("Company Profile was not Found");

        oldCompany.setName(dto.getName());
        oldCompany.setIndustry(dto.getIndustry());
        oldCompany.setTeamSize(dto.getTeamSize());
        oldCompany.setDescription(dto.getDescription());

        companyProfileRepository.save(oldCompany);
    }

    @Transactional
    public void deleteCompanyProfile(Integer companyProfileId) {

        CompanyProfile company = companyProfileRepository.findById(companyProfileId)
                .orElseThrow(() -> new ApiException("Company Profile not found"));

        User user = company.getUser();
        CompanyCredit companyCredit = company.getCompanyCredit();

        companyCredit.setCompanyProfile(null);
        user.setCompanyProfile(null);

        company.setUser(null);
        company.setCompanyCredit(null);

        companyProfileRepository.delete(company);
        companyCreditRepository.delete(companyCredit);
        userRepository.delete(user);
    }

    public List<CompanyFullInfoDTOOut> getAllCompaniesFullDetails(){
        List<CompanyFullInfoDTOOut> dtoOuts = new ArrayList<>();
        for(CompanyProfile c : companyProfileRepository.findAll()){
            dtoOuts.add(convertCompanyFull(c));
        }
        return dtoOuts;
    }

    public CompanyFullInfoDTOOut getCompanyDetails(Integer id){
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(id);
        if(companyProfile != null){
            return convertCompanyFull(companyProfile);
        }
        else return new CompanyFullInfoDTOOut(null,null,null,null,
                null,null,null,null,null);
    }

    public CompanyFullInfoDTOOut convertCompanyFull(CompanyProfile c){
        return new CompanyFullInfoDTOOut(c.getName(),c.getIndustry(),c.getTeamSize(),c.getDescription(),c.getCreatedAt(),
                c.getIsSubscriber(),convertSkillsToDto(c.getSkills()),getAvgReviews(c),c.getReceivedReviews().size());
    }

    public Double getAvgReviews(CompanyProfile companyProfile){
        Double avg = 0.0;
        Double count = 0.0;
        for(Review review : companyProfile.getReceivedReviews()){
            count += review.getRating();
        }
        if(!count.equals(0.0)){
            avg = count / (long) companyProfile.getReceivedReviews().size();
        }
        return avg;
    }

    public List<SkillsDTOOut> convertSkillsToDto(Set<Skills> skills){
        List<SkillsDTOOut> dtoOuts = new ArrayList<>();
        for(Skills s : skills){
            dtoOuts.add(new SkillsDTOOut(s.getName(),s.getDescription()));
        }
        return dtoOuts;
    }

    public List<CompanyProfileDTOOut> convertToDtoOut(List<CompanyProfile> profiles) {
        return profiles.stream()
                .map(profile -> new CompanyProfileDTOOut(
                        profile.getName(),
                        profile.getIndustry(),
                        profile.getTeamSize(),
                        profile.getDescription(),
                        profile.getCreatedAt(),
                        profile.getIsSubscriber()
                ))
                .toList();
    }

    public CompanyFullInfoDTOOut getCompanyById(Integer id){
        return convertCompanyFull(companyProfileRepository.findCompanyProfileById(id));
    }
}
