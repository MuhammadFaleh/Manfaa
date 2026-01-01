package com.v1.manfaa.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CreditTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "double not null")
    private Double amount;

    @Column(name = "created_at", columnDefinition = "timestamp not null")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "varchar(20) not null check(status = 'PENDING' or status='ACCEPTED' or status='CANCELED')")
    private String status;

    @OneToOne
    @JoinColumn(name = "contract_agreement_id", unique = true, nullable = false)
    private ContractAgreement contractAgreement;

    @ManyToOne
    @JoinColumn(name = "paying_company_id", nullable = false)
    private CompanyCredit payingCompany;

    @ManyToOne
    @JoinColumn(name = "paid_company_id", nullable = false)
    private CompanyCredit paidCompany;


}
