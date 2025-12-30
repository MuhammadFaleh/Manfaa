package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.Out.CreditTransactionDTOOut;
import com.v1.manfaa.Model.*;
import com.v1.manfaa.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditTransactionService {

    private final CreditTransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final ContractAgreementRepository contractAgreementRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final CompanyCreditRepository companyCreditRepository;

    public List<CreditTransactionDTOOut> getAllTransactions() {
        return convertToDtoOut(transactionRepository.findAll());
    }

    public List<CreditTransactionDTOOut> getCompanyTransactionsForAdmin(Integer adminId, Integer companyId) {
        userRepository.findById(adminId).orElseThrow(() -> new ApiException("Admin not found"));

        List<CreditTransaction> transactions = transactionRepository
                .findByPayingCompanyIdOrPaidCompanyId(companyId, companyId);
        return convertToDtoOut(transactions);
    }

    public List<CreditTransactionDTOOut> getTransactionsByCompanyId(Integer companyId) {
        List<CreditTransaction> transactions = transactionRepository
                .findByPayingCompanyIdOrPaidCompanyId(companyId, companyId);
        return convertToDtoOut(transactions);
    }

    public void refundCredit(Integer contractId){
        ContractAgreement contractAgreement = contractAgreementRepository.findContractAgreementById(contractId);

        if(contractAgreement == null){
            throw new ApiException("no contract found");
        }

        if(!contractAgreement.getStatus().equalsIgnoreCase("COMPLETED") &&
                !contractAgreement.getStatus().equalsIgnoreCase("DISPUTED")){
            throw new ApiException("contract is not yet closed");
        }
        if(!contractAgreement.getExchangeType().equalsIgnoreCase("TOKENS")){
            throw new ApiException("transaction type is not token based");
        }
        if(contractAgreement.getCreditTransaction().getStatus().equalsIgnoreCase("CANCELED")){
            throw new ApiException("already refunded");
        }

        CompanyProfile companyProfile = contractAgreement.getServiceRequest().getCompanyProfile();
        companyProfile.getCompanyCredit().setBalance(companyProfile.getCompanyCredit().getBalance() + contractAgreement.getCreditTransaction().getAmount());
        contractAgreement.getCreditTransaction().setStatus("CANCELED");
        transactionRepository.save(contractAgreement.getCreditTransaction());
        companyCreditRepository.save(companyProfile.getCompanyCredit());
        companyProfileRepository.save(companyProfile);
    }

    public void addCreditToUser(Integer userId, Double amount){
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(userId);

        if(companyProfile == null){
            throw new ApiException("company not found");
        }
        companyProfile.getCompanyCredit().setBalance(companyProfile.getCompanyCredit().getBalance() + amount);
        companyCreditRepository.save(companyProfile.getCompanyCredit());
    }

    public List<CreditTransactionDTOOut> convertToDtoOut(List<CreditTransaction> transactions) {
        return transactions.stream()
                .map(transaction -> new CreditTransactionDTOOut(
                        transaction.getAmount(),
                        transaction.getCreatedAt(),
                        transaction.getContractAgreement().getId(),
                        transaction.getPayingCompany().getCompanyProfile().getName(),
                        transaction.getPaidCompany().getCompanyProfile().getName()
                ))
                .toList();
    }
}
