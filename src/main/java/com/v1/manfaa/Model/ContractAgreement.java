package com.v1.manfaa.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ContractAgreement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "start_date", columnDefinition = "date not null")
    private LocalDate StartDate;
    @Column(name = "end_date", columnDefinition = "date not null")
    private LocalDate EndDate;
    @Column(name = "is_extended", columnDefinition = "boolean not null")
    private Boolean isExtended;
    @Column(name = "exchange_type", columnDefinition = "varchar(20) not null check(exchange_type = 'TOKENS' or exchange_type='BARTER')")
    private String exchangeType;
    @Column(name = "token_amount", columnDefinition = "double" )
    private Double tokenAmount;
    @Column(columnDefinition = "varchar(20) not null check(status='PENDING' or status = 'ACTIVE' or status='COMPLETED' or status='CANCELLED' or status='DISPUTED')")
    private String status;
    @Column(name = "created_at", columnDefinition = "timestamp not null")
    private LocalDateTime createdAt;
    @Column(name = "closed_at", columnDefinition = "timestamp")
    private LocalDateTime closedAt;
    @Column(name = "first_party_agreement", columnDefinition = "varchar(20) not null check(first_party_agreement= 'PENDING' or " +
            "first_party_agreement = 'ACCEPTED' or first_party_agreement='REJECTED')")
    private String firstPartyAgreement;
    @Column(name = "second_party_agreement", columnDefinition = "varchar(20) not null check(second_party_agreement= 'PENDING' or " +
            "second_party_agreement = 'ACCEPTED' or second_party_agreement='REJECTED')")
    private String secondPartyAgreement;
    @OneToMany(mappedBy = "contractAgreement")
    private Set<Ticket> tickets;

    @OneToOne(mappedBy = "contractAgreement")
    @JsonIgnore
    private CreditTransaction creditTransaction;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "service_request_id")
    private ServiceRequest serviceRequest;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "service_bid_id")
    private ServiceBid serviceBid;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "provider_company_id", nullable = false)
    private CompanyProfile providerCompanyProfile;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "requester_company_id", nullable = false)
    private CompanyProfile requesterCompanyProfile;

    @OneToMany(cascade = CascadeType.ALL , mappedBy = "contractAgreement")
    private Set<Review> reviews;
}
