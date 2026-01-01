package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.ContractAgreementDTOIn;
import com.v1.manfaa.DTO.Out.ContractAgreementDTOOut;
import com.v1.manfaa.Model.*;
import com.v1.manfaa.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractAgreementService {
    private final ContractAgreementRepository contractAgreementRepository;
    private final ServiceBidRepository serviceBidRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final CompanyCreditRepository companyCreditRepository;
    private final CreditTransactionRepository creditTransactionRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public List<ContractAgreementDTOOut> getContracts(){
        List<ContractAgreementDTOOut> dtoOuts = new ArrayList<>();
        for(ContractAgreement contract : contractAgreementRepository.findAll()){
            dtoOuts.add(convertToDTO(contract));
        }
        return dtoOuts;
    }

    public List<ContractAgreementDTOOut> getMyContracts(Integer id){
        List<ContractAgreementDTOOut> dtoOuts = new ArrayList<>();
        for(ContractAgreement contract : contractAgreementRepository.findContractAgreementByCompanyProfileId(id)){
            dtoOuts.add(convertToDTO(contract));
        }
        return dtoOuts;
    }


    public void createContract(ContractAgreementDTOIn contractAgreementDTOIn, Integer companyId){
        // Get company
        CompanyProfile requesterCompany = companyProfileRepository.findCompanyProfileById(companyId);
        if(requesterCompany == null){
            throw new ApiException("Company not found");
        }

        ServiceRequest serviceRequest = serviceRequestRepository.findServiceRequestById(contractAgreementDTOIn.getRequestId());
        ServiceBid serviceBid = serviceBidRepository.findServiceBidById(contractAgreementDTOIn.getBidId());


        if(serviceBid == null){
            throw new ApiException("Service bid not found");
        }
        if(serviceRequest == null){
            throw new ApiException("Service request not found");
        }


        if(!requesterCompany.getId().equals(serviceRequest.getCompanyProfile().getId())){
            throw new ApiException("Unauthorized: You don't own this service request");
        }


        if(!serviceRequest.getStatus().equalsIgnoreCase("CLOSED")){
            throw new ApiException("Request must be CLOSED to create contract");
        }
        if(!serviceBid.getStatus().equalsIgnoreCase("ACCEPTED")){
            throw new ApiException("Bid must be ACCEPTED to create contract");
        }

        if(!serviceRequest.getId().equals(serviceBid.getServiceRequest().getId())){
            throw new ApiException("Bid does not belong to this request");
        }


        if(!contractAgreementRepository.findContractAgreementByServiceBidId(contractAgreementDTOIn.getBidId()).isEmpty()){
            throw new ApiException("Contract already exists for this bid");
        }

        CompanyProfile providerCompany = serviceBid.getCompanyProfile();

        if(requesterCompany.getId().equals(providerCompany.getId())){
            throw new ApiException("Cannot create contract Requester and provider must be different companies");
        }

        ContractAgreement contractAgreement = new ContractAgreement();
        contractAgreement.setStartDate(serviceBid.getProposedStartDate());
        contractAgreement.setEndDate(serviceBid.getProposedEndDate());
        contractAgreement.setIsExtended(false);
        contractAgreement.setExchangeType(serviceBid.getPaymentMethod());
        contractAgreement.setTokenAmount(serviceBid.getTokenAmount());
        contractAgreement.setStatus("PENDING");
        contractAgreement.setCreatedAt(LocalDateTime.now());
        contractAgreement.setFirstPartyAgreement("ACCEPTED");
        contractAgreement.setSecondPartyAgreement("PENDING");

        // Set relationships
        contractAgreement.setServiceRequest(serviceRequest);
        contractAgreement.setServiceBid(serviceBid);
        contractAgreement.setProviderCompanyProfile(providerCompany);
        contractAgreement.setRequesterCompanyProfile(requesterCompany);
        ContractAgreement savedContract = contractAgreementRepository.save(contractAgreement);

        if(serviceBid.getPaymentMethod().equalsIgnoreCase("TOKENS")){
            if(requesterCompany.getCompanyCredit().getBalance() < serviceBid.getTokenAmount()){
                throw new ApiException("Insufficient token balance to create contract");
            }
            holdTokens(
                    requesterCompany,
                    providerCompany,
                    serviceBid.getTokenAmount(),
                    savedContract
            );


        }

        // Send email notification
        String recipientEmail = providerCompany.getUser().getEmail();
        String subject = "Contract Approval Required";
        String message = "Dear " + providerCompany.getName() + ",\n\n"
                + "We are pleased to inform you that your service bid for the request titled \""
                + serviceRequest.getTitle() + "\" has been approved by the service requester.\n\n"
                + "A contract has now been created and requires approval from both you and the service requester. "
                + "Once both parties have approved the contract, it will become active.\n\n"
                + "After activation, please ensure timely communication and delivery of the agreed service.\n\n"
                + "If you have any questions, feel free to contact us.\n\n"
                + "Kind regards,\n"
                + "Support Team";

        emailService.sendEmail(recipientEmail, subject, message);
    }

    public void deleteContract(Integer id, Integer contract_id){
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(id);
        ContractAgreement contractAgreement = contractAgreementRepository.findContractAgreementById(contract_id);


        if(companyProfile == null || contractAgreement == null ||
                !companyProfile.getId().equals(contractAgreement.getRequesterCompanyProfile().getId())){
            throw new ApiException("contract not found");
        }

        if(!contractAgreement.getStatus().equalsIgnoreCase("PENDING")){
            throw new ApiException("contract is already started and can't be delete file a ticket to " +
                    "cancel the contract");
        }
        ServiceBid serviceBid = contractAgreement.getServiceBid();
        ServiceRequest serviceRequest = contractAgreement.getServiceRequest();
        CompanyProfile provider = contractAgreement.getProviderCompanyProfile();
        companyProfile.getRequesterContractAgreement().remove(contractAgreement);
        provider.getProviderContractAgreement().remove(contractAgreement);
        serviceBid.setServiceRequest(null);
        serviceRequest.setContractAgreement(null);
        contractAgreementRepository.delete(contractAgreement);
        serviceRequestRepository.save(serviceRequest);
        serviceBidRepository.save(serviceBid);
        companyProfileRepository.save(provider);
        companyProfileRepository.save(companyProfile);
    }
    public void setAccepted(Integer companyId, Integer contractId){
        ContractAgreement contractAgreement = contractAgreementRepository.findContractAgreementById(contractId);
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(companyId);


        if(contractAgreement == null){
            throw new ApiException("Contract not found");
        }

        if(companyProfile == null){
            throw new ApiException("Company not found");
        }


        if(contractAgreement.getRequesterCompanyProfile().getId().equals(
                contractAgreement.getProviderCompanyProfile().getId())){
            throw new ApiException("Invalid contract: Requester and provider cannot be the same company");
        }

        boolean isRequester = contractAgreement.getRequesterCompanyProfile().getId().equals(companyId);
        boolean isProvider = contractAgreement.getProviderCompanyProfile().getId().equals(companyId);

        if(!isRequester && !isProvider){
            throw new ApiException("Unauthorized: You are not part of this contract");
        }

        // Check contract status
        if(!contractAgreement.getStatus().equalsIgnoreCase("PENDING")){
            throw new ApiException("Contract is not in PENDING status");
        }

        // Set acceptance based on who is accepting
        if(isRequester){
            if(!contractAgreement.getFirstPartyAgreement().equalsIgnoreCase("PENDING")){
                throw new ApiException("Requester has already accepted this contract");
            }
            contractAgreement.setFirstPartyAgreement("ACCEPTED");
        }

        if(isProvider){
            if(!contractAgreement.getSecondPartyAgreement().equalsIgnoreCase("PENDING")){
                throw new ApiException("Provider has already accepted this contract");
            }
            contractAgreement.setSecondPartyAgreement("ACCEPTED");
        }

        if(contractAgreement.getFirstPartyAgreement().equalsIgnoreCase("ACCEPTED") &&
                contractAgreement.getSecondPartyAgreement().equalsIgnoreCase("ACCEPTED")){
                if(contractAgreement.getExchangeType().equalsIgnoreCase("TOKENS")){
                    contractAgreement.setFirstPartyAgreement("DELIVERED");
                }
                contractAgreement.setStatus("ACTIVE");

                String subject = "Contract Activated";
                String message = "Dear User,\n\n"
                        + "We are pleased to inform you that the contract for the service request titled \""
                        + contractAgreement.getServiceRequest().getTitle() + "\" has been approved by both parties and is now active.\n\n"
                        + "You may now proceed according to the terms of the contract. "
                        + "Please ensure clear communication and timely delivery of the agreed service.\n\n"
                        + "If you have any questions or require assistance, feel free to contact us.\n\n"
                        + "Kind regards,\n"
                        + "Support Team";

                emailService.sendEmail(
                        contractAgreement.getRequesterCompanyProfile().getUser().getEmail(),
                        subject,
                        message
                );
                emailService.sendEmail(
                        contractAgreement.getProviderCompanyProfile().getUser().getEmail(),
                        subject,
                        message
                );
        }

        contractAgreementRepository.save(contractAgreement);
    }

    public void setRejected(Integer user_id, Integer contract_id){
        ContractAgreement contractAgreement = contractAgreementRepository.findContractAgreementById(contract_id);
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(user_id);
        String recipientName = "";
        String recipientEmail = "";

        if(companyProfile == null || contractAgreement == null ||
                !contractAgreement.getProviderCompanyProfile().getId().equals(user_id) &&
                !contractAgreement.getRequesterCompanyProfile().getId().equals(user_id) ){
            throw new ApiException("contract not found");
        }

        if(!contractAgreement.getSecondPartyAgreement().equalsIgnoreCase("PENDING")){
            throw new ApiException("contract already checked");
        }

        if(contractAgreement.getProviderCompanyProfile().getId().equals(user_id)){
            contractAgreement.setSecondPartyAgreement("REJECTED");
            recipientName = contractAgreement.getProviderCompanyProfile().getName();
            recipientEmail = contractAgreement.getProviderCompanyProfile().getUser().getEmail();
        }

        String subject = "Contract Cancelled";

        String message = "Dear " + recipientName + ",\n\n"
                + "We would like to inform you that the contract related to the service request titled \""
                + contractAgreement.getServiceRequest().getTitle() + "\" has been cancelled.\n\n"
                + "Since contracts can be cancelled by either party prior to completion, "
                + "this agreement is no longer active and no further action is required.\n\n"
                + "If you believe this cancellation was made in error or have any questions, "
                + "please contact our support team.\n\n"
                + "Kind regards,\n"
                + "Support Team";

        if(contractAgreement.getExchangeType().equalsIgnoreCase("TOKENS")){
            releaseCredit(contractAgreement.getCreditTransaction());
        }

        emailService.sendEmail(recipientEmail, subject, message);
        contractAgreement.setStatus("CANCELLED");
        contractAgreement.getServiceRequest().setStatus("OPEN");
        contractAgreement.setClosedAt(LocalDateTime.now());
        serviceRequestRepository.save(contractAgreement.getServiceRequest());
        contractAgreementRepository.save(contractAgreement);
    }

    public void complete(Integer companyId, Integer contractId, ContractAgreementDTOIn dto){
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(companyId);
        ContractAgreement contractAgreement = contractAgreementRepository.findContractAgreementById(contractId);

        if(companyProfile == null){
            throw new ApiException("Company not found");
        }
        if(contractAgreement == null){
            throw new ApiException("Contract not found");
        }

        boolean isRequester = contractAgreement.getRequesterCompanyProfile().getId().equals(companyId);
        boolean isProvider = contractAgreement.getProviderCompanyProfile().getId().equals(companyId);

        if(!isRequester && !isProvider){
            throw new ApiException("Unauthorized: You are not part of this contract");
        }

        if(!contractAgreement.getStatus().equalsIgnoreCase("ACTIVE")){
            throw new ApiException("Contract is not active");
        }

        // Mark delivery based on who is completing
        if(isProvider){
            if(contractAgreement.getSecondPartyAgreement().equalsIgnoreCase("DELIVERED")){
                throw new ApiException("Provider has already marked delivery as complete");
            }
            contractAgreement.setSecondPartyAgreement("DELIVERED");
            contractAgreement.setSecondPartyDelivered(dto.getDelivery());
        }

        if(isRequester){
            if(contractAgreement.getFirstPartyAgreement().equalsIgnoreCase("DELIVERED")){
                throw new ApiException("Requester has already marked delivery as complete");
            }
            contractAgreement.setFirstPartyAgreement("DELIVERED");
            contractAgreement.setFirstPartyDelivered(dto.getDelivery());
        }

        // Check if both parties have confirmed delivery
        if(contractAgreement.getFirstPartyAgreement().equalsIgnoreCase("DELIVERED") &&
                contractAgreement.getSecondPartyAgreement().equalsIgnoreCase("DELIVERED")){

            contractAgreement.setStatus("COMPLETED");
            contractAgreement.setClosedAt(LocalDateTime.now());

            // Send completion emails
            String subject = "Contract Completed";
            String message = "Dear Parties Involved,\n\n"
                    + "This is to inform you that the contract associated with the service request titled \""
                    + contractAgreement.getServiceRequest().getTitle() + "\" has been successfully completed.\n\n"
                    + "All contractual obligations have been fulfilled, and the agreement is now formally closed.\n\n"
                    +"first party deliveries: " + contractAgreement.getFirstPartyDelivered() +"\n\n"
                    +"second party deliveries: " + contractAgreement.getSecondPartyDelivered() +"\n\n"
                    + "We appreciate the cooperation of both parties throughout the duration of this contract.\n\n"
                    + "If you have any questions or require further assistance, please contact the support team.\n\n"
                    + "Kind regards,\n"
                    + "Support Team";

            emailService.sendEmail(
                    contractAgreement.getProviderCompanyProfile().getUser().getEmail(),
                    subject,
                    message
            );
            emailService.sendEmail(
                    contractAgreement.getRequesterCompanyProfile().getUser().getEmail(),
                    subject,
                    message
            );

            if(contractAgreement.getExchangeType().equalsIgnoreCase("TOKENS")){
                transferCredit(contractAgreement.getCreditTransaction());
            }
        }

        contractAgreementRepository.save(contractAgreement);
    }

    public void extendTime(Integer contractId, Integer userId){
        ContractAgreement contractAgreement = contractAgreementRepository.findContractAgreementById(contractId);
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(userId);

        if(companyProfile == null || contractAgreement == null ||
                !contractAgreement.getProviderCompanyProfile().getId().equals(userId) &&
                !contractAgreement.getRequesterCompanyProfile().getId().equals(userId) ){
            throw new ApiException("contract not found");
        }

        if(!contractAgreement.getStatus().equalsIgnoreCase("ACTIVE")){
            throw new ApiException("contract not active");
        }

        if(contractAgreement.getIsExtended()){
            throw new ApiException("contract already was extended");
        }

        contractAgreement.setIsExtended(true);
        long durationDays = ChronoUnit.DAYS.between(contractAgreement.getStartDate(), contractAgreement.getEndDate());
        contractAgreement.setEndDate(contractAgreement.getEndDate().plusDays(durationDays / 2));
        contractAgreementRepository.save(contractAgreement);
    }

    public void holdTokens(CompanyProfile firstParty, CompanyProfile secondProfile,Double amount,ContractAgreement contractAgreement){
        if(firstParty.getCompanyCredit().getBalance() < amount){
            throw new ApiException("not enough credit");
        }

        CreditTransaction creditTransaction = new CreditTransaction(null,amount,LocalDateTime.now(),"PENDING",
                contractAgreement,firstParty.getCompanyCredit(),secondProfile.getCompanyCredit());

        firstParty.getCompanyCredit().setBalance(firstParty.getCompanyCredit().getBalance() - amount);

        companyProfileRepository.save(firstParty);
        companyCreditRepository.save(firstParty.getCompanyCredit());
        creditTransactionRepository.save(creditTransaction);
        contractAgreement.setCreditTransaction(creditTransaction);
        creditTransactionRepository.save(creditTransaction);
    }

    public void releaseCredit(CreditTransaction creditTransaction){

        if(creditTransaction == null){
            throw new ApiException("credit transaction not found");
        }
        CompanyProfile companyProfile = creditTransaction.getPayingCompany().getCompanyProfile();
        companyProfile.getCompanyCredit().setBalance(companyProfile.getCompanyCredit().getBalance() + creditTransaction.getAmount());
        creditTransaction.setStatus("CANCELLED");

        companyCreditRepository.save(companyProfile.getCompanyCredit());
        creditTransactionRepository.save(creditTransaction);
    }

    public void transferCredit(CreditTransaction creditTransaction){

        if(creditTransaction == null){
            throw new ApiException("credit transaction not found");
        }

        CompanyProfile secondCompany = creditTransaction.getPaidCompany().getCompanyProfile();
        secondCompany.getCompanyCredit().setBalance(secondCompany.getCompanyCredit().getBalance() + creditTransaction.getAmount());
        creditTransaction.setStatus("ACCEPTED");

        companyCreditRepository.save(secondCompany.getCompanyCredit());
        creditTransactionRepository.save(creditTransaction);
    }

    //Todo: Set DISPUTED - (Admin)

    public ContractAgreementDTOOut convertToDTO(ContractAgreement contract){
        return new ContractAgreementDTOOut(contract.getId(),contract.getStartDate(),contract.getEndDate(),
                contract.getIsExtended(),contract.getExchangeType(),contract.getTokenAmount(),contract.getStatus(),
                contract.getCreatedAt(),contract.getClosedAt(), contract.getFirstPartyAgreement(), contract.getSecondPartyAgreement(),
                contract.getServiceRequest(),contract.getServiceBid());
    }
}
