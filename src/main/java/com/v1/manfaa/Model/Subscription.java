package com.v1.manfaa.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(columnDefinition = "timestamp not null")
    private LocalDate startDate;

    @Column(columnDefinition = "timestamp not null")
    private LocalDate endDate;

    @Column(columnDefinition = "boolean not null")
    private Boolean isActive;

    @ManyToOne
    @JsonIgnore
    private CompanyProfile companyProfile;



}
