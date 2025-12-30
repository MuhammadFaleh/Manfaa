package com.v1.manfaa.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@Entity
@Setter
@Getter
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(columnDefinition = "double not null")
    private Double rating;

    @Column(columnDefinition = "text not null")
    private String description;

    @Column(columnDefinition = "timestamp not null")
    private LocalDateTime created_at;


    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "reviewer_id")
    private CompanyProfile reviewerProfile;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "reviewed_company_id")
    private CompanyProfile reviewedProfile;

    @ManyToOne
    @JsonIgnore
    private ContractAgreement contractAgreement;



}
