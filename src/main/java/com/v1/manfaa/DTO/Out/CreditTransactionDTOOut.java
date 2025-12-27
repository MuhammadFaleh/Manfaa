package com.v1.manfaa.DTO.Out;

import com.v1.manfaa.Model.CompanyCredit;
import com.v1.manfaa.Model.ContractAgreement;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditTransactionDTOOut {

    private Double amount;

    private LocalDateTime createdAt;

    private Integer contractId;

    private String payingCompanyName;

    private String paidCompanyName;
}
