package com.v1.manfaa.Service;

import com.v1.manfaa.Model.CompanyCredit;
import com.v1.manfaa.Repository.CompanyCreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyCreditService {

    private final CompanyCreditRepository companyCreditRepository;

    public List<CompanyCredit> getAllCredits() {
        return companyCreditRepository.findAll();
    }

    public CompanyCredit getMyCredit(Integer id){
        return companyCreditRepository.findCompanyCreditByUserId(id);
    }

}
