package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.ContractAgreementDTOIn;
import com.v1.manfaa.DTO.Out.ContractAgreementDTOOut;
import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.ContractAgreement;
import com.v1.manfaa.Model.ServiceBid;
import com.v1.manfaa.Model.ServiceRequest;
import com.v1.manfaa.Repository.CompanyProfileRepository;
import com.v1.manfaa.Repository.ContractAgreementRepository;
import com.v1.manfaa.Repository.ServiceBidRepository;
import com.v1.manfaa.Repository.ServiceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ContractAgreementService {
    private final ContractAgreementRepository contractAgreementRepository;
    private final ServiceBidRepository serviceBidRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final CompanyProfileRepository companyProfileRepository;

    public List<ContractAgreementDTOOut> getContracts(){
        List<ContractAgreementDTOOut> dtoOuts = new ArrayList<>();
        for(ContractAgreement contract : contractAgreementRepository.findAll()){
            dtoOuts.add(convertToDTO(contract));
        }
        return dtoOuts;
    }

    public void createContract(ContractAgreementDTOIn contractAgreementDTOIn, Integer id){
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(id);
        ServiceRequest serviceRequest = serviceRequestRepository.findServiceRequestById(contractAgreementDTOIn.getRequestId());
        ServiceBid serviceBid = serviceBidRepository.findServiceBidById(contractAgreementDTOIn.getBidId());

        if(serviceBid == null || serviceRequest == null){
            throw new ApiException("service bid or request not found");
        }

        if(companyProfile == null || !companyProfile.getId().equals(serviceRequest.getCompanyProfile().getId())){
            throw new ApiException("Unauthorized to make a contract");
        }

        if(!serviceRequest.getStatus().equalsIgnoreCase("CLOSED")
                || !serviceBid.getStatus().equalsIgnoreCase("ACCEPTED")){
            throw new ApiException("request is not closed or bid is not accepted");
        }

        if(!serviceRequest.getId().equals(serviceBid.getServiceRequest().getId())){
            throw new ApiException("not the same request and bid");
        }

        if(!contractAgreementRepository.findContractAgreementByServiceBidId(contractAgreementDTOIn.getBidId()).isEmpty()){
            throw new ApiException("contract already exists");
        }

        ContractAgreement contractAgreement = new ContractAgreement(null, serviceBid.getProposedStartDate(),serviceBid.getProposedEndDate()
                ,false,serviceBid.getPaymentMethod(),serviceBid.getTokenAmount(),"PENDING", LocalDateTime.now(),
                null,"PENDING","PENDING",null,null,
                serviceRequest,serviceBid,serviceBid.getCompanyProfile(),serviceRequest.getCompanyProfile(),null);


        serviceRequest.getCompanyProfile().getRequesterContractAgreement().add(contractAgreement);
        serviceBid.getCompanyProfile().getProviderContractAgreement().add(contractAgreement);
        serviceBid.setContractAgreement(contractAgreement);
        serviceRequest.setContractAgreement(contractAgreement);
        serviceBidRepository.save(serviceBid);
        serviceRequestRepository.save(serviceRequest);
        companyProfileRepository.save(serviceRequest.getCompanyProfile());
        companyProfileRepository.save(serviceBid.getCompanyProfile());
        contractAgreementRepository.save(contractAgreement);
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

    public void setAccepted(Integer user_id, Integer contract_id){
        ContractAgreement contractAgreement = contractAgreementRepository.findContractAgreementById(contract_id);
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(user_id);

        if(companyProfile == null || contractAgreement == null ||
                !contractAgreement.getProviderCompanyProfile().getId().equals(user_id) ||
                !contractAgreement.getRequesterCompanyProfile().getId().equals(user_id) ){
            throw new ApiException("contract not found");
        }

        if(!contractAgreement.getFirstPartyAgreement().equalsIgnoreCase("PENDING") ||
                !contractAgreement.getSecondPartyAgreement().equalsIgnoreCase("PENDING")){
            throw new ApiException("contract already checked");
        }

        if(contractAgreement.getRequesterCompanyProfile().getId().equals(user_id)){
            contractAgreement.setFirstPartyAgreement("ACCEPTED");
        }
        if(contractAgreement.getProviderCompanyProfile().getId().equals(user_id)){
            contractAgreement.setSecondPartyAgreement("ACCEPTED");
        }
        if(contractAgreement.getFirstPartyAgreement().equalsIgnoreCase("ACCEPTED")
                && contractAgreement.getSecondPartyAgreement().equalsIgnoreCase("ACCEPTED")){
            contractAgreement.setStatus("ACTIVE");
        }
        contractAgreementRepository.save(contractAgreement);
    }

    public void setRejected(Integer user_id, Integer contract_id){
        ContractAgreement contractAgreement = contractAgreementRepository.findContractAgreementById(contract_id);
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(user_id);

        if(companyProfile == null || contractAgreement == null ||
                !contractAgreement.getProviderCompanyProfile().getId().equals(user_id) ||
                !contractAgreement.getRequesterCompanyProfile().getId().equals(user_id) ){
            throw new ApiException("contract not found");
        }

        if(!contractAgreement.getFirstPartyAgreement().equalsIgnoreCase("PENDING") ||
                !contractAgreement.getSecondPartyAgreement().equalsIgnoreCase("PENDING")){
            throw new ApiException("contract already checked");
        }


        if(contractAgreement.getProviderCompanyProfile().getId().equals(user_id)){
            contractAgreement.setSecondPartyAgreement("REJECTED");
        }
        if(contractAgreement.getRequesterCompanyProfile().getId().equals(user_id)){
            contractAgreement.setFirstPartyAgreement("REJECTED");
        }
        contractAgreement.setStatus("CANCELLED");
        contractAgreementRepository.save(contractAgreement);
    }

    public ContractAgreementDTOOut convertToDTO(ContractAgreement contract){
        return new ContractAgreementDTOOut(contract.getId(),contract.getStartDate(),contract.getEndDate(),
                contract.getIsExtended(),contract.getExchangeType(),contract.getTokenAmount(),contract.getStatus(),
                contract.getCreatedAt(),contract.getClosedAt(), contract.getFirstPartyAgreement(), contract.getSecondPartyAgreement(),
                contract.getServiceRequest(),contract.getServiceBid());
    }
}
