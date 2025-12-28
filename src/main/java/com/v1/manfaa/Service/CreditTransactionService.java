package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.Out.CompanyProfileDTOOut;
import com.v1.manfaa.DTO.Out.CreditTransactionDTOOut;
import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.ContractAgreement;
import com.v1.manfaa.Model.CreditTransaction;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Repository.ContractAgreementRepository;
import com.v1.manfaa.Repository.CreditTransactionRepository;
import com.v1.manfaa.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditTransactionService {

    private final CreditTransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final ContractAgreementRepository contractAgreementRepository;

    public List<CreditTransactionDTOOut> getAllTransactions() {
        return convertToDtoOut(transactionRepository.findAll());
    }

    public List<CreditTransactionDTOOut> getCompanyTransactionsForAdmin(Integer adminId, Integer companyId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ApiException("Admin not found"));

        if (!"ADMIN".equals(admin.getRole())) {
            throw new ApiException("Unauthorized: User is not an admin");
        }

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

        if(!contractAgreement.getStatus().equalsIgnoreCase("COMPLETED")){
            throw new ApiException("contract is not yet closed");
        }
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
