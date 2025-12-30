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

@AllArgsConstructor
@Entity
@Setter
@Getter
@NoArgsConstructor
public class CompanyProfile {
    @Id
    private Integer id;

    @Column(columnDefinition = "varchar(20) not null")
    private String name;

    @Column(columnDefinition = "varchar(20) not null")
    private String industry;

    @Column(name = "team_size", columnDefinition = "int not null")
    private Integer teamSize;

    @Column(columnDefinition = "TEXT not null")
    private String description;

    @Column(name = "created_at", columnDefinition = "timestamp not null")
    private LocalDateTime createdAt;

    @Column(name = "is_subscriber", columnDefinition = "boolean not null")
    private Boolean isSubscriber;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "company_skills",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skills> skills;

    @OneToMany(mappedBy = "reviewerProfile")
    @JsonIgnore
    private Set<Review> writtenReviews;

    @OneToMany(mappedBy = "reviewedProfile")
    @JsonIgnore
    private Set<Review> receivedReviews;

    @OneToMany(mappedBy = "companyProfile")
    @JsonIgnore
    private Set<Payment> payments;

    @OneToOne(mappedBy = "companyProfile", cascade = CascadeType.ALL)
    private CompanyCredit companyCredit;

    // relationships
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "companyProfile")
    private Set<Subscription> subscriptions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "companyProfile")
    private Set<Ticket> tickets;

    @OneToMany(mappedBy = "companyProfile")
    private Set<ServiceRequest> serviceRequest;

    @OneToMany(mappedBy = "companyProfile")
    private Set<ServiceBid> serviceBid;

    @OneToMany(mappedBy = "providerCompanyProfile")
    private Set<ContractAgreement> providerContractAgreement;

    @OneToMany(mappedBy = "requesterCompanyProfile")
    private Set<ContractAgreement> requesterContractAgreement;
}