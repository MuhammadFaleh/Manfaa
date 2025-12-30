package com.v1.manfaa.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(50) not null")
    private String title;

    @Column(columnDefinition = "TEXT not null")
    private String body;

    @Column(columnDefinition = "varchar(20) not null check(category='CONTRACT' or category='SUGGESTION' or category='SUBSCRIPTION' or category='PLATFORM')")
    private String category;

    @Column(columnDefinition = "varchar(20) not null check(priority = 'HIGH' or priority='MEDIUM' or priority='LOW')")
    private String priority;

    @Column(name = "created_at", columnDefinition = "timestamp not null")
    private LocalDateTime createdAt;

    @Column(name = "resolved_at", columnDefinition = "timestamp")
    private LocalDateTime resolvedAt;

    @Column(name = "resolved_by", columnDefinition = "int")
    private Integer resolvedBy;

    @Column(columnDefinition = "varchar(20) not null check(status = 'OPEN' or status = 'RESOLVED' or status = 'CLOSED')")
    private String status;

    @ManyToOne
    @JsonIgnore
    private CompanyProfile companyProfile;

    @ManyToOne
    @JsonIgnore
    private ContractAgreement contractAgreement;


}
