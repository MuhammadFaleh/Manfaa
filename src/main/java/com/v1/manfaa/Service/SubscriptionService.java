package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.Out.SubscriptionDTOOut;
import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.Payment;
import com.v1.manfaa.Model.Subscription;
import com.v1.manfaa.Repository.CompanyProfileRepository;
import com.v1.manfaa.Repository.PaymentRepository;
import com.v1.manfaa.Repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;

    public List<SubscriptionDTOOut> convertToDtoOut(List<Subscription> subscriptions) {
        return subscriptions.stream()
                .map(subscription -> new SubscriptionDTOOut(
                        subscription.getStartDate(),
                        subscription.getEndDate(),
                        subscription.getIsActive()
                ))
                .toList();
    }

    public List<SubscriptionDTOOut> getAllSubscription() {
        return convertToDtoOut(subscriptionRepository.findAll());
    }


    public void monthlySubscription(Integer companyProfileId) {
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(companyProfileId);
        Subscription issubscription = subscriptionRepository.findSubscriptionByCompanyProfileIdAndIsActive(companyProfileId, true);
        Payment payment = paymentRepository.findFirstByCompanyProfileIdOrderByCreatedAtDesc(companyProfileId);



        if (companyProfile == null) {
            throw new ApiException("company not found");
        }
        if (issubscription != null) {
            throw new ApiException("Subscription Already valid ");
        }

        if(payment == null || !payment.getStatus().equalsIgnoreCase("paid")){
            throw new ApiException("user hasn't payed");
        }

        if(!payment.getAmount().equals(5.0)){
            throw new ApiException("user hasn't payed the correct amount");
        }

        Subscription subscription = new Subscription(null, LocalDate.now(), LocalDate.now().plusMonths(1), true, companyProfile);
        String recipientEmail = companyProfile.getUser().getEmail();
        String subject = "Monthly Subscription Activated";

        String message = "Dear " + companyProfile.getName() + ",\n\n"
                + "Thank you for subscribing to our Monthly Subscription plan!\n\n"
                + "Your subscription is now active and will provide you with full access to our platform features.\n\n"
                + "Subscription Details:\n"
                + "- Plan: Monthly Subscription\n"
                + "- Start Date: " + subscription.getStartDate() + "\n"
                + "- End Date: " + subscription.getEndDate() + "\n"
                + "- Renewal Date: " + subscription.getEndDate() + "\n\n"
                + "Your subscription will automatically renew on the end date unless cancelled.\n\n"
                + "If you have any questions or need assistance, please don't hesitate to contact us.\n\n"
                + "Kind regards,\n"
                + "Support Team";

        emailService.sendEmail(recipientEmail, subject, message);
        companyProfile.getSubscriptions().add(subscription);
        companyProfile.setIsSubscriber(true);


        subscription.setCompanyProfile(companyProfile);
        subscriptionRepository.save(subscription);
        companyProfileRepository.save(companyProfile);




    }


    public void yearlySubscription(Integer companyProfileId) {
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(companyProfileId);
        Subscription issubscription = subscriptionRepository.findSubscriptionByCompanyProfileIdAndIsActive(companyProfileId, true);
        Payment payment = paymentRepository.findFirstByCompanyProfileIdOrderByCreatedAtDesc(companyProfileId);

        if (companyProfile == null) {
            throw new ApiException("companyProfile not found");
        }
        if (issubscription != null) {
            throw new ApiException("Subscription Already valid ");
        }

        if(payment == null || !payment.getStatus().equalsIgnoreCase("paid")){
            throw new ApiException("user hasn't payed");
        }

        if(!payment.getAmount().equals(50.0)){
            throw new ApiException("user hasn't payed the correct amount");
        }



        Subscription subscription = new Subscription(null, LocalDate.now(), LocalDate.now().plusYears(1), true, companyProfile);
        String recipientEmail = companyProfile.getUser().getEmail();
        String subject = "Yearly Subscription Activated";

        String message = "Dear " + companyProfile.getName() + ",\n\n"
                + "Thank you for subscribing to our Monthly Subscription plan!\n\n"
                + "Your subscription is now active and will provide you with full access to our platform features.\n\n"
                + "Subscription Details:\n"
                + "- Plan: Monthly Subscription\n"
                + "- Start Date: " + subscription.getStartDate() + "\n"
                + "- End Date: " + subscription.getEndDate() + "\n"
                + "- Renewal Date: " + subscription.getEndDate() + "\n\n"
                + "Your subscription will automatically renew on the end date unless cancelled.\n\n"
                + "If you have any questions or need assistance, please don't hesitate to contact us.\n\n"
                + "Kind regards,\n"
                + "Support Team";

        emailService.sendEmail(recipientEmail, subject, message);

        companyProfile.getSubscriptions().add(subscription);
        companyProfile.setIsSubscriber(true);
        subscription.setCompanyProfile(companyProfile);
        subscriptionRepository.save(subscription);
        companyProfileRepository.save(companyProfile);


    }

    public void cancelSubscription(Integer companyId) {
        Subscription subscription = subscriptionRepository.findSubscriptionByCompanyProfileIdAndIsActive(companyId,true);
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(companyId);
        Payment payment = paymentRepository.findFirstByCompanyProfileIdOrderByCreatedAtDesc(companyId);

        if (subscription == null) {
            throw new ApiException("Subscription not found");
        }
        if(companyProfile == null){
            throw new ApiException("Company not found");
        }
        if (!subscription.getIsActive()) {
            throw new ApiException("Subscription is already inactive");
        }

        if(payment != null  && payment.getStatus().equalsIgnoreCase("paid")){
            payment.setStatus("cancelled");
            paymentRepository.save(payment);
        }

        subscription.setIsActive(false);
        companyProfile.setIsSubscriber(false);
        String recipientEmail = companyProfile.getUser().getEmail();
        String subject = "Subscription Cancellation";

        String message = "Dear " + companyProfile.getName() + ",\n\n"
                + "We are sorry to see you go\n\n"
                + "Your subscription is now deactivated and will will lose access to the platform features.\n\n"
                + "you will receive the refund within 5 work days:\n"
                + "If you have any questions or need assistance, please don't hesitate to contact us.\n\n"
                + "Kind regards,\n"
                + "Support Team";

        emailService.sendEmail(recipientEmail, subject, message);
        subscriptionRepository.save(subscription);
        companyProfileRepository.save(companyProfile);
    }




}