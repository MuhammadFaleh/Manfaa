package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.Out.SkillsDTOOut;
import com.v1.manfaa.DTO.Out.SubscriptionDTOOut;
import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.Skills;
import com.v1.manfaa.Model.Subscription;
import com.v1.manfaa.Repository.CompanyProfileRepository;
import com.v1.manfaa.Repository.SubscriptionRepository;
import jdk.jshell.JShell;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final CompanyProfileRepository companyProfileRepository;

    public List<SubscriptionDTOOut> convertToDtoOut(List<Subscription> subscriptions) {
        return subscriptions.stream()
                .map(subscription -> new SubscriptionDTOOut(
                       subscription.getStartDate(),
                        subscription.getEndDate(),
                        subscription.getIsActive()
                ))
                .toList();
    }
    public List<SubscriptionDTOOut>getAllSubscription(){
        return convertToDtoOut(subscriptionRepository.findAll());
    }



    public void MonthlySubscription(Integer companyProfileId){
      CompanyProfile companyProfile =  companyProfileRepository.findCompanyProfileById(companyProfileId);
      Subscription issubscription = subscriptionRepository.findSubscriptionByCompanyProfileIdAndIsActive(companyProfileId,true);

      if(companyProfile == null){
          throw new ApiException("companyProfile not found");
      }
      if(issubscription!= null){
          throw new ApiException("Subscription Already valid ");
      }

      Subscription subscription = new Subscription(null,LocalDate.now(),LocalDate.now().plusMonths(1),true,companyProfile);

      companyProfile.getSubscription().add(subscription);

      subscriptionRepository.save(subscription);


    }


    public void YearlySubscription(Integer companyProfileId){
        CompanyProfile companyProfile =  companyProfileRepository.findCompanyProfileById(companyProfileId);
        Subscription issubscription = subscriptionRepository.findSubscriptionByCompanyProfileIdAndIsActive(companyProfileId,true);
        if(companyProfile == null){
            throw new ApiException("companyProfile not found");
        }
        if(issubscription!=null){
            throw new ApiException("Subscription Already valid");
        }

        Subscription subscription = new Subscription(null,LocalDate.now(),LocalDate.now().plusYears(1),true,companyProfile);

        companyProfile.getSubscription().add(subscription);
        subscriptionRepository.save(subscription);


    }




}
