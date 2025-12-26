package com.v1.manfaa.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@AllArgsConstructor
@Entity
@Setter
@Getter
@NoArgsConstructor
public class CompanyProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private LocalDate createdAt;

    @Column(name = "is_subscriber", columnDefinition = "boolean not null")
    private boolean isSubscriber;

    @OneToOne
    @MapsId
  private User user;

    @OneToMany(cascade = CascadeType.ALL , mappedBy = "companyProfile")
    private Set<Skills> skills;

    @OneToMany(mappedBy = "reviewerProfile")
    @JsonIgnore
    private Set<Review> writtenReviews;

    @OneToMany(mappedBy = "reviewedProfile")
    @JsonIgnore
    private Set<Review> receivedReviews;



    // relationships
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "companyProfile")
    private Set<Subscription> subscriptions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "companyProfile")
    private Set<Ticket> tickets;


    @OneToMany(mappedBy = "companyProfile")
    private Set<ServiceRequest> serviceRequest;
    @OneToMany(mappedBy = "companyProfile")
    private Set<ServiceBid> serviceBid;
    @OneToMany(mappedBy = "providerCompanyProfile" )
    private Set<ContractAgreement> providerContractAgreement;
    @OneToMany(mappedBy = "requesterCompanyProfile")
    private Set<ContractAgreement> requesterContractAgreement;

}
