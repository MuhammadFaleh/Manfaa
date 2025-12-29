package com.v1.manfaa.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CompanyCredit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "Double default 0")
    private Double balance;

    @Column(name = "total_spent", columnDefinition = "Double default 0")
    private Double totalSpent;

    @Column(name = "total_earned", columnDefinition = "Double default 0")
    private Double totalEarned;

    @OneToMany(mappedBy = "payingCompany")
    private Set<CreditTransaction> outgoingTransactions;

    @OneToMany(mappedBy = "paidCompany")
    private Set<CreditTransaction> incomingTransactions;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "companyCredit")
    @PrimaryKeyJoinColumn
    @JsonIgnore
    private CompanyProfile companyProfile;

}
